package hirs.utils.signature.cose.Cbor;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Supports COSE rfc 9052 by decoding encoded CBOR structures in a Byte String (CBOR Major Type 2)
 * "The payload is wrapped in a bstr (Byte string - major type 2) to ensure that it is transported without changes. "
 * use getContent() to retrieve the data with the byteSting encoding stripped off
 * Typical encoding is 59 01 XX where XX is a length
 */
public class CborBstr {
    private final byte[] contents;
    private static int typeMask = 0xE0;
    private static int infoMask = 0x1F;
    private static int shiftOffset = 0x05;
    private static int byteStringType = 0x02;
    private static int byteStringLength = 0x03;

    public CborBstr(byte[] data) {

        byte type = data[0];
        // Check if byte 0 is of major type 0x02 (Byte String)
        byte cborType = (byte) ((type & typeMask) >> shiftOffset);
        if (cborType != byteStringType) {
            throw new RuntimeException("Byte Array Decode Error, expecting a byte String (Type 2) but found " + cborType);
        }
        contents = new byte[data.length - byteStringLength];
        System.arraycopy(data, byteStringLength, contents, 0, data.length - byteStringLength);
    }

    public static boolean isByteString(byte[] data) {
        byte type = data[0];
        // Check if byte 0 is of major type 0x02 (Byte String)
        byte cborType = (byte) ((type & typeMask) >> shiftOffset);
        if (cborType == byteStringType) return true;
        return false;
    }

    public static boolean isEmptyByteString(byte[] data) {
        if (!isByteString(data)) return false;
        // per the cose spec 0xa0 is equivalent to {}
        return (data[3] & 0xFF) == 0xA0;
    }

    /**
     * process byte string length rfc 8489
     * @param data
     * @return
     */
    public static int getByteStringLength(byte[] data){
        int length = 0;
        byte type = data[0];
        byte tagInfo = (byte) (type & infoMask);
        if (tagInfo < 0x18) length = tagInfo; // values 0 to 0x17
        else if (tagInfo == 0x18) length = (int) data[1];
        else if (tagInfo == 0x19) {
            byte[] tmpArray = {0,0,data[1] ,data[2]};
            ByteBuffer buf = ByteBuffer.wrap(tmpArray);
            length = buf.getInt();
        }
        else if (tagInfo == 0x1a) {
            byte[] tmpArray = {data[1] ,data[2], data[3], data[4]};
            ByteBuffer buf = ByteBuffer.wrap(tmpArray);
            length = buf.getInt();
        }
        return length;
    }

    /**
     * Determines length of the byte sting header per rfc 8489
     * @param data
     * @return
     */
    public static int getByteStringTagLength(byte[] data){
        int length = 0;
        byte type = data[0];
        byte tagInfo = (byte) (type & infoMask);
        if (tagInfo < 0x18) length = 1; // values 0 to 0x17
        else if (tagInfo == 0x18) length = 2;
        else if (tagInfo == 0x19) {
            length = 3;
        }
        else if (tagInfo == 0x1a) {
            length = 4;
        }
        return length;
    }

    public static byte[] removeByteStringIfPresent(byte[] data) {
        if (!isByteString(data)) return data;
        int length = getByteStringTagLength(data);
        byte[] contents = new byte[data.length - length];
        
        System.arraycopy(data, length, contents, 0, data.length - length);
        return contents;
    }

    public byte[] getContents() {
        return contents.clone(); // returns a copy, not the original
    }
}
