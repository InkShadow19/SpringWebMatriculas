package pe.villaesperanza.SpringWebMatriculas.service;

import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class TimeTravelService {

    private Instant customTime = null;

    public Instant getNow() {
        return customTime != null ? customTime : Instant.now();
    }

    public void setCustomTime(Instant customTime) {
        this.customTime = customTime;
    }

    public void resetTime() {
        this.customTime = null;
    }
}