package hirs.utils.rim.unsignedRim.cbor.ietfCoswid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class   CoswidConfigValidator {
    protected JsonNode configRootNode = null;
    protected Coswid coswidRef = new Coswid();
    protected CoswidItems coswidItems = new CoswidItems();
    protected boolean isValid = true;
    @Getter
    protected String invalidFields = "";
    @Getter
    protected int invalidFieldCount = 0;
    public CoswidConfigValidator () {
    }

    public boolean isValid(JsonNode rootNode) {
        configRootNode = rootNode;
        boolean validity = true;
        List<String> keys = new ArrayList<>();
        getAllKeysUsingJsonNodeFields(rootNode, keys);
        for (String key : keys) {
            if( !isValidKey(key)) {
                validity = false;
            }
        }
        return validity;
    }
    
    protected boolean isValidKey(String key) {
        int index = coswidItems.getIndex(key);
        boolean validity = true;
        if (index == CoswidItems.UNKNOWN_INT) {
            validity = false;
            invalidFields += key + " ";
            invalidFieldCount++;
        }
        return validity;
    }

    private boolean extraFieldCheck() {
        return false;
    }

    private boolean payloadCheck() {
        return false;
    }

    private void getAllKeysUsingJsonNodeFields(JsonNode jsonNode, List<String> keys) {
        //List<String> keys = new ArrayList<>();
        if (jsonNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            fields.forEachRemaining(field -> {
                keys.add(field.getKey());
                getAllKeysUsingJsonNodeFields((JsonNode) field.getValue(), keys);
            });
        } else if (jsonNode.isArray()) {
            ArrayNode arrayField = (ArrayNode) jsonNode;
            arrayField.forEach(node -> {
                getAllKeysUsingJsonNodeFields(node, keys);
            });
        }
    }
}
