package sinnet.app.ports.in;

import java.util.UUID;

public interface Report3PortIn {
    
    public byte[] downloadPdfFile(UUID projectId);
}
