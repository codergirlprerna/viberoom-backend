// ── CreateRoomRequest.java ──
package com.viberoom.backend.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoomRequest {
    @NotBlank private String name;
    @NotBlank private String mood;
    private boolean isPublic = true;

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public @NotBlank String getMood() {
        return mood;
    }

    public void setMood(@NotBlank String mood) {
        this.mood = mood;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
