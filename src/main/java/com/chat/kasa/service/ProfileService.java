package com.chat.kasa.service;

import com.chat.kasa.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    Profile createProfile(Profile profile);

    Optional<Profile> getProfileById(Long id);

    Optional<Profile> getProfileByUsername(String username);

    List<Profile> getAllProfiles();
}
