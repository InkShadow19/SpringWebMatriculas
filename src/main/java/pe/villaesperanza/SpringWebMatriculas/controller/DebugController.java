package pe.villaesperanza.SpringWebMatriculas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.villaesperanza.SpringWebMatriculas.service.TimeTravelService;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final TimeTravelService timeTravelService;

    public DebugController(TimeTravelService timeTravelService) {
        this.timeTravelService = timeTravelService;
    }

    @PostMapping("/set-time")
    public ResponseEntity<String> setCustomTime(@RequestBody Map<String, String> payload) {
        Instant newTime = Instant.parse(payload.get("time"));
        timeTravelService.setCustomTime(newTime);
        return ResponseEntity.ok("Tiempo simulado establecido a: " + newTime);
    }

    @PostMapping("/reset-time")
    public ResponseEntity<String> resetTime() {
        timeTravelService.resetTime();
        return ResponseEntity.ok("Tiempo del sistema restaurado al actual.");
    }
}