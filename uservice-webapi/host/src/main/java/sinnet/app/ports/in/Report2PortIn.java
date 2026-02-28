package sinnet.app.ports.in;

import java.util.UUID;

public interface Report2PortIn {
    
    public byte[] downloadPdfFile(UUID projectId, int yearFrom, int monthFrom, int yearTo, int monthTo);
}
