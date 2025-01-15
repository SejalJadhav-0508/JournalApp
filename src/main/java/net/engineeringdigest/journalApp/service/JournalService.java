package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.repository.JournalRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalService {
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private UserService userService;

    public List<JournalEntity> getAll(){
        return journalRepository.findAll();
    }


    @Transactional
    public void saveJournalEntry(JournalEntity journalEntity, String username){
        try {
            journalEntity.setDate(LocalDateTime.now());
            UserEntity user = userService.findByUserName(username);
            JournalEntity saved = journalRepository.save(journalEntity);
            user.getJournalEntities().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveJournalEntry(JournalEntity journalEntity){
        journalRepository.save(journalEntity);
    }

    public Optional<JournalEntity> getById(ObjectId journalId){
        return journalRepository.findById(journalId);
    }

    @Transactional
    public boolean deleteById(String username, ObjectId journalId){
        boolean removed = false;
        try {
            UserEntity user = userService.findByUserName(username);
            removed = user.getJournalEntities().removeIf(x -> x.getId().equals(journalId));
            if (removed) {
                userService.saveUser(user);
                journalRepository.deleteById(journalId);
            }
            return removed;
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException("An error occured while deleting the entry : ",e);
        }
    }

//    public List<JournalEntity> findByUsername(String username){
//
//    }

//    public JournalEntity updateEntry()
}
