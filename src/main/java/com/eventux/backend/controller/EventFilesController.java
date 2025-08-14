package com.eventux.backend.controller;

import com.eventux.backend.model.Event;
import com.eventux.backend.model.Files;
import com.eventux.backend.model.User;
import com.eventux.backend.repository.EventRepository;
import com.eventux.backend.repository.FilesRepository;
import com.eventux.backend.repository.InvitedRepository;
import com.eventux.backend.security.AuthFacade;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/files")
public class EventFilesController {
    private final EventRepository events;
    private final FilesRepository files;
    private final InvitedRepository invited;
    private final AuthFacade auth;

    public EventFilesController(EventRepository events,
                                FilesRepository files,
                                InvitedRepository invited,
                                AuthFacade auth) {
        this.events = events;
        this.files = files;
        this.invited = invited;
        this.auth = auth;
    }

    // -------- helpers

    private Event findEvent(Integer eventId) {
        return events.findById(eventId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    private boolean isHost(User me, Event ev) {
        return ev.getHost() != null && me.getIdUser() != null
                && me.getIdUser().equals(ev.getHost().getIdUser());
    }

    private boolean isManager(User me, Event ev) {
        return ev.getManager() != null && me.getIdUser() != null
                && me.getIdUser().equals(ev.getManager().getIdUser());
    }

    private boolean isInvited(User me, Integer eventId) {
        // registered guest whose email appears in invited list for this event
        if (me.getEmail() == null) return false;
        return invited.existsByEvent_EventIDAndId_EmailIgnoreCase(eventId, me.getEmail());
    }

    private boolean canView(User me, Event ev, Integer eventId) {
        return isHost(me, ev) || isManager(me, ev) || isInvited(me, eventId);
    }

    private boolean canUpload(User me, Event ev, Integer eventId) {
        // host, manager, invited
        return canView(me, ev, eventId);
    }

    private boolean canModify(User me, Event ev) {
        // ONLY host can edit/delete
        return isHost(me, ev);
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Forbidden for this event"));
    }

    // -------- API

    // List metadata (visible to host/manager/invited)
    @GetMapping
    public ResponseEntity<?> list(@PathVariable Integer eventId) {
        var me = auth.meOrThrow();
        var ev = findEvent(eventId);
        if (!canView(me, ev, eventId)) return forbidden();

        var list = files.findByEvent_EventID(eventId).stream()
                .map(f -> Map.of(
                        "id", f.getFileID(),
                        "name", f.getFileName(),
                        "size", f.getFile_Size(),
                        "type", f.getFile_type(),
                        "uploader", Map.of(
                                "id", f.getUser() != null ? f.getUser().getIdUser() : null,
                                "email", f.getUser() != null ? f.getUser().getEmail() : null
                        )
                ))
                .toList();

        return ResponseEntity.ok(list);
    }

    // Upload (host/manager/invited)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@PathVariable Integer eventId,
                                    @RequestParam("files") MultipartFile[] parts) {
        var me = auth.meOrThrow();
        var ev = findEvent(eventId);
        if (!canUpload(me, ev, eventId)) return forbidden();

        for (MultipartFile mf : parts) {
            try {
                var ent = new Files();
                ent.setFileName(mf.getOriginalFilename());
                ent.setFile_Size(String.valueOf(mf.getSize()));
                ent.setFile_type(mf.getContentType());
                ent.setUser(me);
                ent.setEvent(ev);
                ent.setContent(mf.getBytes());
                files.save(ent);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Failed to save: " + mf.getOriginalFilename()));
            }
        }
        return list(eventId);
    }

    // Download (host/manager/invited)
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Integer eventId,
                                           @PathVariable Integer fileId) {
        var me = auth.meOrThrow();
        var ev = findEvent(eventId);
        if (!canView(me, ev, eventId)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        var f = files.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (f.getEvent() == null || !eventId.equals(f.getEvent().getEventID())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String filename = f.getFileName() != null ? f.getFileName() : ("file-" + f.getFileID());
        String dispo = "attachment; filename*=UTF-8''" +
                URLEncoder.encode(filename, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.getFile_type() != null ? f.getFile_type() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, dispo)
                .body(f.getContent());
    }

    // Delete (ONLY host)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> delete(@PathVariable Integer eventId,
                                    @PathVariable Integer fileId) {
        var me = auth.meOrThrow();
        var ev = findEvent(eventId);
        if (!canModify(me, ev)) return forbidden();

        var f = files.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        if (f.getEvent() == null || !eventId.equals(f.getEvent().getEventID())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "File does not belong to this event"));
        }

        files.delete(f);
        return ResponseEntity.noContent().build();
    }

    // (Optional) Rename (ONLY host)
    @PatchMapping("/{fileId}")
    public ResponseEntity<?> rename(@PathVariable Integer eventId,
                                    @PathVariable Integer fileId,
                                    @RequestBody Map<String, String> body) {
        var me = auth.meOrThrow();
        var ev = findEvent(eventId);
        if (!canModify(me, ev)) return forbidden();

        var f = files.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        if (f.getEvent() == null || !eventId.equals(f.getEvent().getEventID())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "File does not belong to this event"));
        }

        var newName = body.get("name");
        if (newName == null || newName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing 'name'"));
        }
        f.setFileName(newName.trim());
        files.save(f);
        return ResponseEntity.ok(Map.of("id", f.getFileID(), "name", f.getFileName()));
    }
}
