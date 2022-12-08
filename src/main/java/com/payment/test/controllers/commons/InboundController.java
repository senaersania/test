package com.payment.test.controllers.commons;

import com.payment.test.payload.response.MessageResponse;
import com.payment.test.repository.common.InboundRepository;
import com.payment.test.models.commons.Inbound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/v1/inbound")
public class InboundController {
    @Autowired
    InboundRepository inboundRepository;

    @GetMapping("/getInbound")
    public ResponseEntity<Object> getAll() {
        log.info("[InboundController::getInbound] start");
        List<Inbound> inboundList = new ArrayList<Inbound>();
        inboundList = inboundRepository.findAll();
        log.info("[InboundController::getInbound] end");
        return new ResponseEntity<>(inboundList, HttpStatus.OK);
    }

    @PostMapping("/createInbound")
    public ResponseEntity<?> create(@RequestBody Inbound inbound) {
        log.info("[InboundController::createInbound] start");
        Inbound inboundResponse = null;
        try {
            inboundResponse = inboundRepository.save(inbound);
            return new ResponseEntity<>(inboundResponse, HttpStatus.OK);
        } catch (Exception exception){
            log.error("InboundController::createInbound {}", exception);
        }
        log.info("[InboundController::createInbound] end");
        return new ResponseEntity<Inbound>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/updateInbound/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> edit(@PathVariable("id") Long id, @RequestBody Inbound inbound) {
        log.info("[InboundController::updateInbound] start");
        try {
            Optional<Inbound> inboundData = inboundRepository.findById(id);
            if (inboundData.isPresent()){
                Inbound inboundUpdate = inboundData.get();
                log.info("[InboundUpdateStart: {}", inboundUpdate.toString());
                inboundUpdate.setChannel(inbound.getChannel());
                inboundUpdate.setContent(inbound.getContent());
                inboundUpdate.setType(inbound.getType());
                log.info("[InboundUpdateEnd : {}", inboundUpdate.toString());
                return new ResponseEntity<>(inboundRepository.save(inboundUpdate), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            log.info("[InboundController::updateInbound] end {}", exception);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteInbound/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        log.info("[InboundController::deleteInbound] start");
        try {
            inboundRepository.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Data Has Been Success to Delete!"), HttpStatus.OK);
        } catch (Exception exception) {
            log.info("InboundController::deleteInbound] end {}", exception);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
