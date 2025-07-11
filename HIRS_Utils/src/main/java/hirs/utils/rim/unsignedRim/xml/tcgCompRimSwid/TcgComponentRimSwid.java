package hirs.utils.rim.unsignedRim.xml.tcgCompRimSwid;

import hirs.utils.rim.unsignedRim.xml.pcclientrim.PcClientRim;

public class TcgComponentRimSwid extends PcClientRim {

    TcgComponentRimSwidBuilder builder = new TcgComponentRimSwidBuilder();

    public void create (String configFile, String rimEventLog , String certificateFile, String privateKeyFile, boolean embeddedCert, String outFile) {

        builder.setAttributesFile(configFile);
        builder.setRimEventLog(rimEventLog);

        builder.setDefaultCredentials(false);
        builder.setPemCertificateFile(certificateFile);
        builder.setPemPrivateKeyFile(privateKeyFile);
        if (embeddedCert) {
            builder.setEmbeddedCert(true);
        }
        /* skip timestamp for now

        List<String> timestampArguments = commander.getTimestampArguments();
        if (timestampArguments.size() > 0) {
            if (new TimestampArgumentValidator(timestampArguments).isValid()) {
                gateway.setTimestampFormat(timestampArguments.get(0));
                if (timestampArguments.size() > 1) {
                    gateway.setTimestampArgument(timestampArguments.get(1));
                }
            } else {
                exitWithErrorCode("The provided timestamp argument(s) " +
                        "is/are not valid.");
            }
        }
        */
        builder.generateSwidTag(outFile);
    }
}
