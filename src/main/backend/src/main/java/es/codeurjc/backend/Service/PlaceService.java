package es.codeurjc.backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.Model.Place;
import es.codeurjc.backend.Repository.PlaceRepository;

@Service
public class PlaceService {
    @Autowired 
    private PlaceRepository placeRepository;
    public List<Place> getAllPlaces() {
         List<Place> places = placeRepository.findAll();
            return places;
    }
         
}
