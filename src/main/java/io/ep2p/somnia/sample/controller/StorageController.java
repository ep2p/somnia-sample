package io.ep2p.somnia.sample.controller;

import io.ep2p.somnia.sample.domain.DataModel;
import io.ep2p.somnia.sample.service.StorageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class StorageController {

    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }


    @PostMapping("/storage/store/{id}")
    public @ResponseBody
    String storeData(@RequestBody DataModel dataModel, @PathVariable("id") long id){
        return storageService.store(id, dataModel);
    }

    @GetMapping("/storage/get/{id}")
    public @ResponseBody
    DataModel getData(@PathVariable("id") long id){
        return storageService.get(id);
    }
}
