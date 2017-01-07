package de.stphngrtz.models;

import com.google.gson.annotations.Expose;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.Objects;
import java.util.UUID;

public class Profil {

    @Expose
    public final String id;

    public final String token;

    @Expose
    public final String name;

    @Expose
    public final String farbe;

    private Profil(String id, String token, String name, String farbe) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.farbe = farbe;
    }

    public Profil withId() {
        return new Profil(UUID.randomUUID().toString(), token, name, farbe);
    }

    public Profil withId(String id) {
        return new Profil(id, token, name, farbe);
    }

    public Profil withToken(String token) {
        return new Profil(id, token, name, farbe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profil profil = (Profil) o;
        return Objects.equals(id, profil.id) &&
                Objects.equals(token, profil.token) &&
                Objects.equals(name, profil.name) &&
                Objects.equals(farbe, profil.farbe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, name, farbe);
    }

    public static class Codec implements org.bson.codecs.Codec<Profil> {

        public static class Fields {
            public static final String id = "_id";
            public static final String token = "token";
            public static final String name = "name";
            public static final String farbe = "farbe";
        }

        @Override
        public Profil decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            String id = reader.readString(Fields.id);
            String token = reader.readString(Fields.token);
            String name = reader.readString(Fields.name);
            String farbe = reader.readString(Fields.farbe);
            reader.readEndDocument();
            return new Profil(id, token, name, farbe);
        }

        @Override
        public void encode(BsonWriter writer, Profil value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            writer.writeString(Fields.id, value.id);
            writer.writeString(Fields.token, value.token);
            writer.writeString(Fields.name, value.name);
            writer.writeString(Fields.farbe, value.farbe);
            writer.writeEndDocument();
        }

        @Override
        public Class<Profil> getEncoderClass() {
            return Profil.class;
        }
    }
}
