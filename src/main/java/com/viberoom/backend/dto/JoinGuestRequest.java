package com.viberoom.backend.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinGuestRequest {
    public @NotBlank String getGuestName() {
        return guestName;
    }

    public void setGuestName(@NotBlank String guestName) {
        this.guestName = guestName;
    }

    @NotBlank private String guestName;
}
