package de.stphngrtz.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import de.stphngrtz.models.Profil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfilController {

    private final MongoCollection<Profil> profileCollection;

    public ProfilController(MongoDatabase mongoDatabase) {
        profileCollection = mongoDatabase.getCollection("profile", Profil.class);
    }

    public List<Profil> getProfile(String token) {
        return profileCollection.find(Filters.eq(Profil.Codec.Fields.token, token)).into(new ArrayList<>());
    }

    public Optional<Profil> getProfil(String token, String id) {
        return Optional.ofNullable(profileCollection.find(Filters.and(
                Filters.eq(Profil.Codec.Fields.token, token),
                Filters.eq(Profil.Codec.Fields.id, id)
        )).first());
    }

    public Profil postProfil(String token, Profil profil) {
        Profil profilMitIdUndToken = profil.withId().withToken(token);
        profileCollection.insertOne(profilMitIdUndToken);
        return profilMitIdUndToken;
    }

    public void putProfil(String token, String id, Profil profil) {
        Profil profilMitIdUndToken = profil.withId(id).withToken(token);
        profileCollection.replaceOne(Filters.and(
                Filters.eq(Profil.Codec.Fields.token, token),
                Filters.eq(Profil.Codec.Fields.id, id)
        ), profilMitIdUndToken, new UpdateOptions().upsert(true));
    }

    public void deleteProfil(String token, String id) {
        profileCollection.deleteOne(Filters.and(
                Filters.eq(Profil.Codec.Fields.token, token),
                Filters.eq(Profil.Codec.Fields.id, id)
        ));
    }
}
