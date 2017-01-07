package de.stphngrtz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import de.stphngrtz.controllers.ProfilController;
import de.stphngrtz.controllers.ZeitController;
import de.stphngrtz.models.Profil;
import de.stphngrtz.models.Zeit;
import de.stphngrtz.utils.ZonedDateTimeDeserializer;
import de.stphngrtz.utils.ZonedDateTimeSerializer;
import org.apache.commons.cli.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static spark.Spark.*;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final String DEFAULT_HTTP_PORT = "4567";
    private static final String DEFAULT_MONGODB_HOST = "localhost";
    private static final String DEFAULT_MONGODB_PORT = "27017";
    private static final String DEFAULT_MONGODB_DATABASE = "zeiterfassung";
    private static final String DEFAULT_MONGODB_PAUSE = "0";

    private static final String TOKEN_HEADER = "Token";

    public static void main(String[] args) throws ParseException, InterruptedException {

        Options options = new Options();
        options.addOption(Option.builder().longOpt("http-port").hasArg().desc("Port für HTTP Protokoll (default:" + DEFAULT_HTTP_PORT + ")").build());
        options.addOption(Option.builder().longOpt("db-host").hasArg().desc("Host der MongoDB (default:" + DEFAULT_MONGODB_HOST + ")").build());
        options.addOption(Option.builder().longOpt("db-port").hasArg().desc("Port der MongoDB (default:" + DEFAULT_MONGODB_PORT + ")").build());
        options.addOption(Option.builder().longOpt("db-name").hasArg().desc("Name der Datenbank (default:" + DEFAULT_MONGODB_DATABASE + ")").build());
        options.addOption(Option.builder().longOpt("db-pause").hasArg().desc("Pause in ms, bis Verbindung zur Datenbank hergestellt wird (default:" + DEFAULT_MONGODB_PAUSE + "ms)").build());
        options.addOption(Option.builder().longOpt("help").desc("Zeigt diese Hilfe.").build());

        DefaultParser p = new DefaultParser();
        CommandLine cl = p.parse(options, args);

        if (cl.hasOption("help")) {
            new HelpFormatter().printHelp("-", options);
            System.exit(0);
        }

        Integer pause = Integer.valueOf(cl.getOptionValue("db-pause", DEFAULT_MONGODB_PAUSE));
        if (pause > 0)
            Thread.sleep(2000);

        MongoClient mongoClient = new MongoClient(
                new ServerAddress(
                        cl.getOptionValue("db-host", DEFAULT_MONGODB_HOST),
                        Integer.valueOf(cl.getOptionValue("db-port", DEFAULT_MONGODB_PORT))
                ),
                MongoClientOptions.builder().codecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(new Zeit.Codec()),
                        CodecRegistries.fromCodecs(new Profil.Codec()),
                        MongoClient.getDefaultCodecRegistry()
                )).build()
        );
        MongoDatabase mongoDatabase = mongoClient.getDatabase(cl.getOptionValue("db-name", DEFAULT_MONGODB_DATABASE));

        port(Integer.valueOf(cl.getOptionValue("http-port", DEFAULT_HTTP_PORT)));

        options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        before((req, res) -> {
            if (req.requestMethod().equals("OPTIONS"))
                return;

            String token = req.headers(TOKEN_HEADER);
            if (token == null) {
                log.debug("Token fehlt! {} {}", req.requestMethod(), req.pathInfo());
                halt(HttpStatus.UNAUTHORIZED_401);
            }
        });

        exception(Exception.class, (exception, request, response) -> {
            log.info(exception.getMessage());
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            log.info(sw.toString());
        });

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                .create();

        ZeitController zeitController = new ZeitController(mongoDatabase);
        ProfilController profilController = new ProfilController(mongoDatabase);

        get("/profile", (req, res) -> {
            List<Profil> profile = profilController.getProfile(req.headers(TOKEN_HEADER));
            log.debug("Profile angefordert: {}", profile.size());

            res.status(HttpStatus.OK_200);
            return profile;
        }, gson::toJson);
        get("/profile/:id", (req, res) -> {
            String profilId = req.params("id");
            Optional<Profil> profil = profilController.getProfil(req.headers(TOKEN_HEADER), profilId);
            log.debug("Profil angefordert: {} {}", profilId, profil.map(Object::toString).orElse("n/a"));

            if (profil.isPresent()) {
                res.status(HttpStatus.OK_200);
                return profil.get();
            } else {
                res.status(HttpStatus.NOT_FOUND_404);
                return "";
            }
        }, gson::toJson);
        post("/profile", (req, res) -> {
            Profil profil = gson.fromJson(req.body(), Profil.class);
            profil = profilController.postProfil(req.headers(TOKEN_HEADER), profil);
            log.debug("Profil erstellt: {}", profil.id);

            res.header(HttpHeader.LOCATION.name(), profil.id);
            res.status(HttpStatus.CREATED_201);
            return profil;
        }, gson::toJson);
        put("/profile/:id", (req, res) -> {
            String profilId = req.params("id");
            Profil profil = gson.fromJson(req.body(), Profil.class);
            profilController.putProfil(req.headers(TOKEN_HEADER), profilId, profil);
            log.debug("Profil aktualisiert: {}", profilId);

            res.status(HttpStatus.OK_200);
            return "";
        });
        delete("/profile/:id", (req, res) -> {
            String profilId = req.params("id");
            List<Zeit> zeiten = zeitController.getZeiten(req.headers(TOKEN_HEADER), profilId);
            if (zeiten.isEmpty()) {
                profilController.deleteProfil(req.headers(TOKEN_HEADER), profilId);
                log.debug("Profil gelöscht: {}", profilId);
                res.status(HttpStatus.OK_200);
            }
            else {
                log.debug("Profil konnte nicht gelöscht werden: {} ({} Zeiten)", profilId, zeiten.size());
                res.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
            }
            return "";
        });

        get("/zeiten", (req, res) -> {
            List<Zeit> zeiten = zeitController.getZeiten(req.headers(TOKEN_HEADER));
            log.debug("Zeiten angefordert: {}", zeiten.size());

            res.status(HttpStatus.OK_200);
            return zeiten;
        }, gson::toJson);
        get("/zeiten/:id", (req, res) -> {
            String zeitId = req.params("id");
            Optional<Zeit> zeit = zeitController.getZeit(req.headers(TOKEN_HEADER), zeitId);
            log.debug("Zeit angefordert: {} {}", zeitId, zeit.map(Object::toString).orElse("n/a"));

            if (zeit.isPresent()) {
                res.status(HttpStatus.OK_200);
                return zeit.get();
            } else {
                res.status(HttpStatus.NOT_FOUND_404);
                return "";
            }
        }, gson::toJson);
        post("/zeiten", (req, res) -> {
            log.debug(req.body());
            Zeit zeit = gson.fromJson(req.body(), Zeit.class);
            zeit = zeitController.postZeit(req.headers(TOKEN_HEADER), zeit);
            log.debug("Zeit erstellt: {}", zeit.id);

            res.header(HttpHeader.LOCATION.name(), zeit.id);
            res.status(HttpStatus.CREATED_201);
            return zeit;
        }, gson::toJson);
        put("/zeiten/:id", (req, res) -> {
            String zeitId = req.params("id");
            Zeit zeit = gson.fromJson(req.body(), Zeit.class);
            zeitController.putZeit(req.headers(TOKEN_HEADER), zeitId, zeit);
            log.debug("Zeit aktualisiert: {}", zeitId);

            res.status(HttpStatus.OK_200);
            return "";
        });
        delete("/zeiten/:id", (req, res) -> {
            String zeitId = req.params("id");
            zeitController.deleteZeit(req.headers(TOKEN_HEADER), zeitId);
            log.debug("Zeit gelöscht: {}", zeitId);

            res.status(HttpStatus.OK_200);
            return "";
        });
    }
}
