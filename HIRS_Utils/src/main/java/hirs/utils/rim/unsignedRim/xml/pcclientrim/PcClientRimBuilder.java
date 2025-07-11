package hirs.utils.rim.unsignedRim.xml.pcclientrim;

import hirs.utils.swid.CredentialParser;
import hirs.utils.swid.SwidTagConstants;
import hirs.utils.xjc.Directory;
import hirs.utils.xjc.Entity;
import hirs.utils.xjc.File;
import hirs.utils.xjc.FilesystemItem;
import hirs.utils.xjc.Link;
import hirs.utils.xjc.ObjectFactory;
import hirs.utils.xjc.ResourceCollection;
import hirs.utils.xjc.SoftwareIdentity;
import hirs.utils.xjc.SoftwareMeta;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.Generated;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PcClientRimBuilder {

    protected final ObjectFactory objectFactory = new ObjectFactory();
    protected Marshaller marshaller;
    protected String attributesFile;
    protected boolean defaultCredentials;
    protected String jksTruststoreFile;
    protected String pemPrivateKeyFile;
    protected String pemCertificateFile;
    protected boolean embeddedCert;
    protected String rimEventLog;
    protected String timestampFormat;
    protected String timestampArgument;
    protected String errorRequiredFields;
    protected DocumentBuilderFactory dbf;
    protected DocumentBuilder builder;

    public PcClientRimBuilder() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("hirs.utils.xjc");
            this.marshaller = jaxbContext.createMarshaller();
            this.attributesFile = "";
            this.defaultCredentials = true;
            this.pemCertificateFile = "";
            this.embeddedCert = false;
            this.rimEventLog = "";
            this.timestampFormat = "";
            this.timestampArgument = "";
            this.errorRequiredFields = "";
            this.dbf = DocumentBuilderFactory.newInstance();
            this.dbf.setNamespaceAware(true);
            this.builder = this.dbf.newDocumentBuilder();
        } catch (JAXBException e) {
            System.out.println("Error initializing jaxbcontext: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("Error instantiating Document object for parsing swidtag: " + e.getMessage());
            System.exit(1);
        }

    }

    public void generateSwidTag(String filename) {
        Document swidtag = this.builder.newDocument();
        SoftwareIdentity softwareIdentity = null;

        try {
            InputStream is = new FileInputStream(this.attributesFile);
            JsonReader reader = Json.createReader(is);
            JsonObject configProperties = reader.readObject();
            reader.close();
            softwareIdentity = this.createSwidTag(configProperties.getJsonObject("SoftwareIdentity"));
            JAXBElement<Entity> entity = this.objectFactory.createSoftwareIdentityEntity(this.createEntity(configProperties.getJsonObject("Entity")));
            softwareIdentity.getEntityOrEvidenceOrLink().add(entity);
            JAXBElement<Link> link = this.objectFactory.createSoftwareIdentityLink(this.createLink(configProperties.getJsonObject("Link")));
            softwareIdentity.getEntityOrEvidenceOrLink().add(link);
            JAXBElement<SoftwareMeta> meta = this.objectFactory.createSoftwareIdentityMeta(this.createSoftwareMeta(configProperties.getJsonObject("Meta")));
            softwareIdentity.getEntityOrEvidenceOrLink().add(meta);
            swidtag = this.convertToDocument(this.objectFactory.createSoftwareIdentity(softwareIdentity));
            Element rootElement = swidtag.getDocumentElement();
            Node payloadNode = swidtag.importNode(this.assembleCompositePayload(configProperties).getDocumentElement(), true);
            rootElement.appendChild(payloadNode);
            if (this.errorRequiredFields.isEmpty()) {
                Document signedSoftwareIdentity = this.signXMLDocument(swidtag);
                this.writeSwidTagFile(signedSoftwareIdentity, filename);
            } else {
                PrintStream var10000 = System.out;
                String var10001 = this.errorRequiredFields;
                int var10003 = this.errorRequiredFields.length();
                var10000.println("The following fields cannot be empty or null: " + var10001.substring(0, var10003 - 2));
                System.exit(1);
            }
        } catch (JsonException e) {
            System.out.println("Error reading JSON attributes: " + e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist or cannot be read: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    private Document assembleCompositePayload(JsonObject configProperties) throws Exception {
        Directory directory = this.createDirectory(configProperties.getJsonObject("Payload").getJsonObject("Directory"));
        JAXBElement<FilesystemItem> jaxbDirectory = this.objectFactory.createPayloadDirectory(directory);
        Document dirDoc = this.convertToDocument(jaxbDirectory);
        JsonArray files = configProperties.getJsonObject("Payload").getJsonObject("Directory").getJsonArray("File");
        Iterator itr = files.iterator();

        while(itr.hasNext()) {
            File file = this.createFile((JsonObject)itr.next());
            JAXBElement<FilesystemItem> jaxbFile = this.objectFactory.createDirectoryFile(file);
            Document fileDoc = this.convertToDocument(jaxbFile);
            Node fileNode = dirDoc.importNode(fileDoc.getDocumentElement(), true);
            dirDoc.getDocumentElement().appendChild(fileNode);
        }

        ResourceCollection payload = this.createPayload(configProperties.getJsonObject("Payload"));
        JAXBElement<ResourceCollection> jaxbPayload = this.objectFactory.createSoftwareIdentityPayload(payload);
        Document payloadDoc = this.convertToDocument(jaxbPayload);
        Node dirNode = payloadDoc.importNode(dirDoc.getDocumentElement(), true);
        payloadDoc.getDocumentElement().appendChild(dirNode);
        return payloadDoc;
    }

    public void writeSwidTagFile(Document swidTag, String output) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source source = new DOMSource(swidTag);
            if (output.isEmpty()) {
                transformer.transform(source, new StreamResult(System.out));
            } else {
                transformer.transform(source, new StreamResult(new FileOutputStream(output)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + e.getMessage());
        } catch (TransformerConfigurationException e) {
            System.out.println("Error instantiating TransformerFactory class: " + e.getMessage());
        } catch (TransformerException e) {
            System.out.println("Error instantiating Transformer class: " + e.getMessage());
        }

    }

    private SoftwareIdentity createSwidTag(JsonObject jsonObject) {
        SoftwareIdentity swidTag = this.objectFactory.createSoftwareIdentity();
        if (jsonObject == null) {
            this.errorRequiredFields = this.errorRequiredFields + "SoftwareIdentity, ";
        } else {
            swidTag.setLang("en");
            String name = jsonObject.getString("name", "");
            if (!name.isEmpty()) {
                swidTag.setName(name);
            }

            String tagId = jsonObject.getString("tagId", "");
            if (!tagId.isEmpty()) {
                swidTag.setTagId(tagId);
            }

            swidTag.setTagVersion(new BigInteger(jsonObject.getString("tagVersion", "0")));
            swidTag.setVersion(jsonObject.getString("version", "0.0"));
            swidTag.setCorpus(jsonObject.getBoolean("corpus", false));
            swidTag.setPatch(jsonObject.getBoolean("patch", false));
            swidTag.setSupplemental(jsonObject.getBoolean("supplemental", false));
            if (!swidTag.isCorpus() && !swidTag.isPatch() && !swidTag.isSupplemental() && swidTag.getVersion() != "0.0") {
                swidTag.setVersionScheme(jsonObject.getString("versionScheme", "multipartnumeric"));
            }
        }

        return swidTag;
    }

    private Entity createEntity(JsonObject jsonObject) {
        boolean isTagCreator = false;
        Entity entity = this.objectFactory.createEntity();
        if (jsonObject == null) {
            this.errorRequiredFields = this.errorRequiredFields + "Entity, ";
        } else {
            String name = jsonObject.getString("name", "");
            if (!name.isEmpty()) {
                entity.setName(name);
            }

            String[] roles = jsonObject.getString("role", "").split(",");

            for(int i = 0; i < roles.length; ++i) {
                entity.getRole().add(roles[i]);
                if (roles[i].equals("tagCreator")) {
                    isTagCreator = true;
                }
            }

            if (isTagCreator) {
                String regid = jsonObject.getString("regid", "");
                if (!regid.isEmpty()) {
                    entity.setRegid(regid);
                }
            } else {
                entity.setRegid(jsonObject.getString("regid", "invalid.unavailable"));
            }

            String thumbprint = jsonObject.getString("thumbprint", "");
            if (!thumbprint.isEmpty()) {
                entity.setThumbprint(thumbprint);
            }
        }

        return entity;
    }

    private Link createLink(JsonObject jsonObject) {
        Link link = this.objectFactory.createLink();
        String href = jsonObject.getString("href", "");
        if (!href.isEmpty()) {
            link.setHref(href);
        }

        String rel = jsonObject.getString("rel", "");
        if (!rel.isEmpty()) {
            link.setRel(rel);
        }

        return link;
    }

    protected SoftwareMeta createSoftwareMeta(JsonObject jsonObject) {
        SoftwareMeta softwareMeta = this.objectFactory.createSoftwareMeta();
        Map<QName, String> attributes = softwareMeta.getOtherAttributes();
        this.addNonNullAttribute(attributes, SwidTagConstants._COLLOQUIAL_VERSION, jsonObject.getString("colloquialVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._EDITION, jsonObject.getString("edition", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PRODUCT, jsonObject.getString("product", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._REVISION, jsonObject.getString("revision", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PAYLOAD_TYPE, jsonObject.getString("PayloadType", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PLATFORM_MANUFACTURER_STR, jsonObject.getString("platformManufacturerStr", ""), true);
        this.addNonNullAttribute(attributes, SwidTagConstants._PLATFORM_MANUFACTURER_ID, jsonObject.getString("platformManufacturerId", ""), true);
        this.addNonNullAttribute(attributes, SwidTagConstants._PLATFORM_MODEL, jsonObject.getString("platformModel", ""), true);
        this.addNonNullAttribute(attributes, SwidTagConstants._PLATFORM_VERSION, jsonObject.getString("platformVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_MANUFACTURER_STR, jsonObject.getString("firmwareManufacturerStr", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_MANUFACTURER_ID, jsonObject.getString("firmwareManufacturerId", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_MODEL, jsonObject.getString("firmwareModel", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._FIRMWARE_VERSION, jsonObject.getString("firmwareVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._BINDING_SPEC, jsonObject.getString("bindingSpec", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._BINDING_SPEC_VERSION, jsonObject.getString("bindingSpecVersion", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PC_URI_LOCAL, jsonObject.getString("pcURIlocal", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._PC_URI_GLOBAL, jsonObject.getString("pcURIGlobal", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._RIM_LINK_HASH, jsonObject.getString("rimLinkHash", ""));
        return softwareMeta;
    }

    private ResourceCollection createPayload(JsonObject jsonObject) {
        ResourceCollection payload = this.objectFactory.createResourceCollection();
        Map<QName, String> attributes = payload.getOtherAttributes();
        if (jsonObject == null) {
            this.errorRequiredFields = this.errorRequiredFields + "Payload, ";
        } else {
            this.addNonNullAttribute(attributes, SwidTagConstants._N8060_ENVVARPREFIX, jsonObject.getString(SwidTagConstants._N8060_ENVVARPREFIX.getLocalPart(), ""));
            this.addNonNullAttribute(attributes, SwidTagConstants._N8060_ENVVARSUFFIX, jsonObject.getString(SwidTagConstants._N8060_ENVVARSUFFIX.getLocalPart(), ""));
            this.addNonNullAttribute(attributes, SwidTagConstants._N8060_PATHSEPARATOR, jsonObject.getString(SwidTagConstants._N8060_PATHSEPARATOR.getLocalPart(), ""));
        }

        return payload;
    }

    private Directory createDirectory(JsonObject jsonObject) {
        Directory directory = this.objectFactory.createDirectory();
        directory.setName(jsonObject.getString("name", ""));
        Map<QName, String> attributes = directory.getOtherAttributes();
        String supportRimFormat = jsonObject.getString("supportRIMFormat", "supportRIMFormat missing");
        if (!supportRimFormat.equals("supportRIMFormat missing")) {
            if (supportRimFormat.isEmpty()) {
                attributes.put(SwidTagConstants._SUPPORT_RIM_FORMAT, "TCG_EventLog_Assertion");
            } else {
                attributes.put(SwidTagConstants._SUPPORT_RIM_FORMAT, supportRimFormat);
            }
        }

        this.addNonNullAttribute(attributes, SwidTagConstants._SUPPORT_RIM_TYPE, jsonObject.getString("supportRIMType", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._SUPPORT_RIM_URI_GLOBAL, jsonObject.getString("supportRIMURIGlobal", ""));
        return directory;
    }

    private File createFile(JsonObject jsonObject) throws Exception {
        File file = this.objectFactory.createFile();
        file.setName(jsonObject.getString("name", ""));
        file.setSize(new BigInteger(jsonObject.getString("size", "0")));
        Map<QName, String> attributes = file.getOtherAttributes();
        this.addNonNullAttribute(attributes, SwidTagConstants.SHA_256_HASH, jsonObject.getString("hash"), true);
        String supportRimFormat = jsonObject.getString("supportRIMFormat", "supportRIMFormat missing");
        if (!supportRimFormat.equals("supportRIMFormat missing")) {
            if (supportRimFormat.isEmpty()) {
                attributes.put(SwidTagConstants._SUPPORT_RIM_FORMAT, "TCG_EventLog_Assertion");
            } else {
                attributes.put(SwidTagConstants._SUPPORT_RIM_FORMAT, supportRimFormat);
            }
        }

        this.addNonNullAttribute(attributes, SwidTagConstants._SUPPORT_RIM_TYPE, jsonObject.getString("supportRIMType", ""));
        this.addNonNullAttribute(attributes, SwidTagConstants._SUPPORT_RIM_URI_GLOBAL, jsonObject.getString("supportRIMURIGlobal", ""));
        return file;
    }

    protected void addNonNullAttribute(Map<QName, String> attributes, QName key, String value, boolean required) {
        if (required && value.isEmpty()) {
            String var10001 = this.errorRequiredFields;
            this.errorRequiredFields = var10001 + key.getLocalPart() + ", ";
        } else {
            this.addNonNullAttribute(attributes, key, value);
        }

    }

    protected void addNonNullAttribute(Map<QName, String> attributes, QName key, String value) {
        if (!value.isEmpty()) {
            attributes.put(key, value);
        }

    }

    private Document convertToDocument(JAXBElement element) {
        Document doc = null;

        try {
            doc = this.builder.newDocument();
            this.marshaller.marshal(element, doc);
        } catch (JAXBException e) {
            System.out.println("Error while marshaling swidtag: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return doc;
    }

    private Document signXMLDocument(Document doc) {
        XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM");
        List xmlObjectList = null;
        String signatureId = null;
        Reference documentRef = null;

        try {
            documentRef = sigFactory.newReference("", sigFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", (DigestMethodParameterSpec)null), Collections.singletonList(sigFactory.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec)null)), (String)null, (String)null);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            System.out.println("Error while creating enveloped signature Reference: " + ((GeneralSecurityException)e).getMessage());
            System.exit(1);
        }

        List<Reference> refList = new ArrayList();
        refList.add(documentRef);
        if (!this.timestampFormat.isEmpty()) {
            Reference timestampRef = null;

            try {
                timestampRef = sigFactory.newReference("#TST", sigFactory.newDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256", (DigestMethodParameterSpec)null));
            } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
                System.out.println("Error while creating timestamp Reference: " + ((GeneralSecurityException)e).getMessage());
                System.exit(1);
            }

            refList.add(timestampRef);
            xmlObjectList = Collections.singletonList(this.createXmlTimestamp(doc, sigFactory));
            signatureId = "RimSignature";
        }

        SignedInfo signedInfo = null;

        try {
            signedInfo = sigFactory.newSignedInfo(sigFactory.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec)null), sigFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", (SignatureMethodParameterSpec)null), refList);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            System.out.println("Error while creating SignedInfo: " + ((GeneralSecurityException)e).getMessage());
            System.exit(1);
        }

        List<XMLStructure> keyInfoElements = new ArrayList();
        KeyInfoFactory kiFactory = sigFactory.getKeyInfoFactory();
        CredentialParser cp = new CredentialParser();
        PrivateKey privateKey;
        if (this.defaultCredentials) {
            cp.parseJKSCredentials(this.jksTruststoreFile);
            privateKey = cp.getPrivateKey();
        } else {
            try {
                cp.parsePEMCredentials(this.pemCertificateFile, this.pemPrivateKeyFile);
            } catch (Exception e) {
                System.out.println("Error while parsing PEM files: " + e.getMessage());
                System.exit(1);
            }

            X509Certificate certificate = cp.getCertificate();
            privateKey = cp.getPrivateKey();
            if (this.embeddedCert) {
                ArrayList<Object> x509Content = new ArrayList();
                x509Content.add(certificate.getSubjectX500Principal().getName());
                x509Content.add(certificate);
                X509Data data = kiFactory.newX509Data(x509Content);
                keyInfoElements.add(data);
            } else {
                try {
                    keyInfoElements.add(kiFactory.newKeyValue(certificate.getPublicKey()));
                } catch (KeyException e) {
                    System.out.println("Error while creating KeyValue: " + e.getMessage());
                }
            }
        }

        try {
            KeyName keyName = kiFactory.newKeyName(cp.getCertificateSubjectKeyIdentifier());
            keyInfoElements.add(keyName);
        } catch (IOException e) {
            System.out.println("Error while getting SKID: " + e.getMessage());
            System.exit(1);
        }

        KeyInfo keyinfo = kiFactory.newKeyInfo(keyInfoElements);
        DOMSignContext context = new DOMSignContext(privateKey, doc.getDocumentElement());
        XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyinfo, xmlObjectList, signatureId, (String)null);

        try {
            signature.sign(context);
        } catch (XMLSignatureException | MarshalException e) {
            System.out.println("Error while signing the swidtag: " + ((Exception)e).getMessage());
        }

        return doc;
    }

    private XMLObject createXmlTimestamp(Document doc, XMLSignatureFactory sigFactory) {
        Element timeStampElement = null;
        switch (this.timestampFormat.toUpperCase()) {
            case "RFC3852":
                try {
                    byte[] counterSignature = Base64.getEncoder().encode(Files.readAllBytes(Paths.get(this.timestampArgument)));
                    timeStampElement = doc.createElementNS("https://www.ietf.org/rfc/rfc3852.txt", "rcf3852:TimeStamp");
                    timeStampElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:rcf3852", "https://www.ietf.org/rfc/rfc3852.txt");
                    timeStampElement.setAttributeNS("https://www.ietf.org/rfc/rfc3852.txt", "rcf3852:dateTime", new String(counterSignature));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
            case "RFC3339":
                timeStampElement = doc.createElementNS("https://www.ietf.org/rfc/rfc3339.txt", "rcf3339:TimeStamp");
                timeStampElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:rcf3339", "https://www.ietf.org/rfc/rfc3339.txt");
                if (this.timestampArgument.isEmpty()) {
                    timeStampElement.setAttributeNS("https://www.ietf.org/rfc/rfc3339.txt", "rcf3339:dateTime", LocalDateTime.now().toString());
                } else {
                    timeStampElement.setAttributeNS("https://www.ietf.org/rfc/rfc3339.txt", "rcf3339:dateTime", this.timestampArgument);
                }
        }

        DOMStructure timestampObject = new DOMStructure(timeStampElement);
        SignatureProperty signatureProperty = sigFactory.newSignatureProperty(Collections.singletonList(timestampObject), "RimSignature", "TST");
        SignatureProperties signatureProperties = sigFactory.newSignatureProperties(Collections.singletonList(signatureProperty), (String)null);
        XMLObject xmlObject = sigFactory.newXMLObject(Collections.singletonList(signatureProperties), (String)null, (String)null, (String)null);
        return xmlObject;
    }

    @Generated
    public void setAttributesFile(String attributesFile) {
        this.attributesFile = attributesFile;
    }

    @Generated
    public void setDefaultCredentials(boolean defaultCredentials) {
        this.defaultCredentials = defaultCredentials;
    }

    @Generated
    public void setJksTruststoreFile(String jksTruststoreFile) {
        this.jksTruststoreFile = jksTruststoreFile;
    }

    @Generated
    public void setPemPrivateKeyFile(String pemPrivateKeyFile) {
        this.pemPrivateKeyFile = pemPrivateKeyFile;
    }

    @Generated
    public void setPemCertificateFile(String pemCertificateFile) {
        this.pemCertificateFile = pemCertificateFile;
    }

    @Generated
    public void setEmbeddedCert(boolean embeddedCert) {
        this.embeddedCert = embeddedCert;
    }

    @Generated
    public void setRimEventLog(String rimEventLog) {
        this.rimEventLog = rimEventLog;
    }

    @Generated
    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    @Generated
    public void setTimestampArgument(String timestampArgument) {
        this.timestampArgument = timestampArgument;
    }
}
