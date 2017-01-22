package de.stphngrtz.models;

import com.google.gson.annotations.Expose;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class Zeit {

    public enum Typ {
        ARBEIT,
        URLAUB,
        KRANK
    }

    @Expose
    public final String id;

    public final String token;

    @Expose
    public final ZonedDateTime von;

    @Expose
    public final ZonedDateTime bis;

    @Expose
    public final Typ typ;

    @Expose
    public final String bemerkung;

    @Expose
    public final String profilId;

    private Zeit(String id, String token, ZonedDateTime von, ZonedDateTime bis, Typ typ, String bemerkung, String profilId) {
        this.id = id;
        this.token = token;
        this.von = von;
        this.bis = bis;
        this.typ = typ;
        this.bemerkung = bemerkung;
        this.profilId = profilId;
    }

    public Zeit withId() {
        return new Zeit(UUID.randomUUID().toString(), token, von, bis, typ, bemerkung, profilId);
    }

    public Zeit withId(String id) {
        return new Zeit(id, token, von, bis, typ, bemerkung, profilId);
    }

    public Zeit withToken(String token) {
        return new Zeit(id, token, von, bis, typ, bemerkung, profilId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zeit zeit = (Zeit) o;
        return Objects.equals(id, zeit.id) &&
                Objects.equals(token, zeit.token) &&
                Objects.equals(von, zeit.von) &&
                Objects.equals(bis, zeit.bis) &&
                Objects.equals(typ, zeit.typ) &&
                Objects.equals(bemerkung, zeit.bemerkung) &&
                Objects.equals(profilId, zeit.profilId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, von, bis, typ, bemerkung, profilId);
    }

    public static class Codec implements org.bson.codecs.Codec<Zeit> {

        public static class Fields {
            public static final String id = "_id";
            private static final String version = "_version";
            public static final String token = "token";
            public static final String von = "von";
            public static final String bis = "bis";
            public static final String typ = "typ";
            public static final String bemerkung = "bemerkung";
            public static final String profilId = "profilId";
        }

        @Override
        public Zeit decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            String id = reader.readString(Fields.id);

            Zeit zeit;
            if (!Fields.version.equals(reader.readName()))
                zeit = decodeV0(id, reader);
            else {
                int version = reader.readInt32();
                switch (version) {
                    case 1:
                        zeit = decodeV1(id, reader);
                        break;
                    default:
                        throw new IllegalStateException("Unbekannte Version: " + version);
                }
            }
            reader.readEndDocument();
            return zeit;
        }

        private static Zeit decodeV0(String id, BsonReader reader) {
            String token = reader.readString();
            String von = reader.readString(Fields.von);
            String bis = reader.readString(Fields.bis);
            String bemerkung = reader.readString(Fields.bemerkung);
            String profilId = reader.readString(Fields.profilId);

            return new Zeit(id, token, ZonedDateTime.parse(von, DateTimeFormatter.ISO_OFFSET_DATE_TIME), ZonedDateTime.parse(bis, DateTimeFormatter.ISO_OFFSET_DATE_TIME), Typ.ARBEIT, bemerkung, profilId);
        }

        private static Zeit decodeV1(String id, BsonReader reader) {
            String token = reader.readString(Fields.token);
            String von = reader.readString(Fields.von);
            String bis = reader.readString(Fields.bis);
            Typ typ = Typ.valueOf(reader.readString(Fields.typ));
            String bemerkung = reader.readString(Fields.bemerkung);
            String profilId = reader.readString(Fields.profilId);

            return new Zeit(id, token, ZonedDateTime.parse(von, DateTimeFormatter.ISO_OFFSET_DATE_TIME), ZonedDateTime.parse(bis, DateTimeFormatter.ISO_OFFSET_DATE_TIME), typ, bemerkung, profilId);
        }

        @Override
        public void encode(BsonWriter writer, Zeit value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            writer.writeString(Fields.id, value.id);
            writer.writeInt32(Fields.version, 1);
            writer.writeString(Fields.token, value.token);
            writer.writeString(Fields.von, value.von.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            writer.writeString(Fields.bis, value.bis.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            writer.writeString(Fields.typ, value.typ.name());
            writer.writeString(Fields.bemerkung, value.bemerkung);
            writer.writeString(Fields.profilId, value.profilId);
            writer.writeEndDocument();
        }

        @Override
        public Class<Zeit> getEncoderClass() {
            return Zeit.class;
        }
    }
}
