package es.codeurjc.backend.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.backend.model.Place;
import es.codeurjc.backend.repository.PlaceRepository;

@Service
public class PlaceService {
    @Autowired 
    private PlaceRepository placeRepository;

    public List<Place> getAllPlaces() {
         List<Place> places = placeRepository.findAll();
            return places;
    }

    public long placeCount(){
        return placeRepository.count();
    }

    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    public Optional<Place> findById(Long placeId) {
        return placeRepository.findById(placeId);
    }     
}