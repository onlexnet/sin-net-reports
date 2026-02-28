package sinnet.app.ports.in;

import java.time.YearMonth;
import java.util.UUID;

public interface Report2PortIn {
    
    public byte[] downloadPdfFile(UUID projectId, YearMonth from, YearMonth to);
}
