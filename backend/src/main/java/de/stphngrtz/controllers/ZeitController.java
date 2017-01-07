package de.stphngrtz.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import de.stphngrtz.models.Zeit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZeitController {

    private final MongoCollection<Zeit> zeitenCollection;

    public ZeitController(MongoDatabase mongoDatabase) {
        zeitenCollection = mongoDatabase.getCollection("zeiten", Zeit.class);
    }

    public List<Zeit> getZeiten(String token) {
        return zeitenCollection.find(Filters.eq(Zeit.Codec.Fields.token, token)).into(new ArrayList<>());
    }

    public List<Zeit> getZeiten(String token, String profilId) {
        return zeitenCollection.find(Filters.and(
                Filters.eq(Zeit.Codec.Fields.token, token),
                Filters.eq(Zeit.Codec.Fields.profilId, profilId)
        )).into(new ArrayList<>());
    }

    public Optional<Zeit> getZeit(String token, String id) {
        return Optional.ofNullable(zeitenCollection.find(Filters.and(
                Filters.eq(Zeit.Codec.Fields.token, token),
                Filters.eq(Zeit.Codec.Fields.id, id)
        )).first());
    }

    public Zeit postZeit(String token, Zeit zeit) {
        Zeit zeitMitIdUndToken = zeit.withId().withToken(token);
        zeitenCollection.insertOne(zeitMitIdUndToken);
        return zeitMitIdUndToken;
    }

    public void putZeit(String token, String id, Zeit zeit) {
        Zeit zeitMitIdUndToken = zeit.withId(id).withToken(token);
        zeitenCollection.replaceOne(Filters.and(
                Filters.eq(Zeit.Codec.Fields.token, token),
                Filters.eq(Zeit.Codec.Fields.id, id)
        ), zeitMitIdUndToken, new UpdateOptions().upsert(true));
    }

    public void deleteZeit(String token, String id) {
        zeitenCollection.deleteOne(Filters.and(
                Filters.eq(Zeit.Codec.Fields.token, token),
                Filters.eq(Zeit.Codec.Fields.id, id)
        ));
    }
}
