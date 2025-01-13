package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.service.JournalService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private JournalService journalService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntity>> getAllJournalEntriesOfUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity user = userService.findByUserName(username);
            List<JournalEntity> journalEntity = user.getJournalEntities();
            return new ResponseEntity<>(journalEntity, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<JournalEntity> createEntry(@RequestBody JournalEntity newEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            journalService.saveJournalEntry(newEntry, username);
            return new ResponseEntity<>(newEntry, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{journalId}")
    public ResponseEntity<Optional<JournalEntity>> getById(@PathVariable ObjectId journalId){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity userInDB = userService.findByUserName(username);
            List<JournalEntity> collect = userInDB.getJournalEntities().stream().filter(x -> x.getId().equals(journalId)).collect(Collectors.toList());
            if(!collect.isEmpty()){
            Optional<JournalEntity> journalEntity = journalService.getById(journalId);
            if (journalEntity != null){
                return new ResponseEntity<>(journalEntity, HttpStatus.OK);
            }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/id/{journalId}")
    public ResponseEntity<?> deleteEntry(@PathVariable ObjectId journalId){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            boolean removed = journalService.deleteById(username, journalId);
            if (removed) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/id/{journalId}")
    public ResponseEntity<JournalEntity> updateEntry(
            @PathVariable ObjectId journalId,
            @RequestBody JournalEntity newJournalEntry)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity userInDB = userService.findByUserName(username);
            List<JournalEntity> collect = userInDB.getJournalEntities().stream().filter(x -> x.getId().equals(journalId)).collect(Collectors.toList());
            if(!collect.isEmpty()){
                Optional<JournalEntity> journalEntity = journalService.getById(journalId);
                if (journalEntity != null){
                    JournalEntity oldEntity = journalEntity.get();
                    oldEntity.setTitle(null != newJournalEntry.getTitle() ? newJournalEntry.getTitle() : oldEntity.getTitle());
                    oldEntity.setContent(null != newJournalEntry.getContent() ? newJournalEntry.getContent() : oldEntity.getContent());
                    journalService.saveJournalEntry(oldEntity);
                    return new ResponseEntity<>(oldEntity, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
