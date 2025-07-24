package com.pratikmane.wechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pratikmane.wechat.service.DemoDataService;

@RestController
@RequestMapping("/demo")
@CrossOrigin("*")
public class DemoDataController {

    @Autowired
    private DemoDataService demoDataService;

    @PostMapping("/populate")
    public ResponseEntity<String> populateDemoData() {
        try {
            demoDataService.populateAllDemoData();
            return new ResponseEntity<>("✅ Demo data populated successfully! Your WeChat Social platform is now ready for demonstration with comprehensive sample data including users, posts, stories, reels, messages, and more!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("❌ Error populating demo data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<String> clearAllData() {
        try {
            demoDataService.clearAllData();
            return new ResponseEntity<>("🗑️ All data cleared successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("❌ Error clearing data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
