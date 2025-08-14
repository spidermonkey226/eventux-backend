package com.eventux.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class EventCreationRequest {
    private String eventName;
    private String eventCatgory;

    private AddressDTO address;
    private List<TableDTO> tables;
    private List<InviteDTO> inviteList;

    private String managerEmail;     // optional
    private String managerFirstName; // optional
    private String managerPhone;     // optional

    @Data
    public static class AddressDTO {
        private String city;
        private String street_name;
        private String street_number;
        private String post_code;
    }

    @Data
    public static class TableDTO {
        private int table_number;
        private int chair_count;
    }

    @Data
    public static class InviteDTO {
        private String email;
        private String firstName;
        private String note;
    }
}
