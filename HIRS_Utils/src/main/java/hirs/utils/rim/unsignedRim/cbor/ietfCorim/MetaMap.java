package hirs.utils.rim.unsignedRim.cbor.ietfCorim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import hirs.utils.signature.cose.Cbor.CborBstr;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

public class  MetaMap {
    Map<String, Object> parsedData = null;
    protected JsonNode rootNode = null;
    @Setter
    @Getter
    String signerName = "";
    @Setter
    @Getter
    String signerUri = "";
    @Setter
    @Getter
    long notBefore = 0;
    @Setter
    @Getter
    String notBeforeStr = "";
    @Setter
    @Getter
    long notAfter = 0;
    @Setter
    @Getter
    String notAfterStr = "";

    /**
     * Process the corim-signer-map from section 4.2.2.1 of the IETF Corim spec
     * @param mapData
     */
    public MetaMap(byte[] mapData) {
        ObjectMapper mapper = new ObjectMapper(new CBORFactory());
        Format format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        ZonedDateTime dateTime = null;
        try {
            byte[] map = CborBstr.removeByteStringIfPresent(mapData);
            parsedData = mapper.readValue(new ByteArrayInputStream(map), Map.class);
           // System.out.println(parsedData);
            rootNode = mapper.readTree(map);
            signerName = rootNode.path("0").get("0").textValue(); // Signer Name
            if ( rootNode.path("0").get("1") != null ) {
                signerUri = rootNode.path("0").get("1").textValue(); // Signer URI
            }
            if ( rootNode.path("1").get("0") != null) {  // not before
                notBefore = rootNode.path("1").get("0").longValue();
                Date date = new Date(notBefore * 1000);
                notBeforeStr = format.format(date);
            }
            if ( rootNode.path("1").get("1") != null) {  // not before
                notAfter = rootNode.path("1").get("1").longValue();
                Date date = new Date(notAfter * 1000);
                notAfterStr = format.format(date);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // System.out.println(parsedData);

    }
}
