package com.example.demo.interfaces;

import java.util.List;

import com.example.demo.entity.PersonaEntity;

public interface IPersonaService {
	
	List<PersonaEntity> findAll();

	PersonaEntity findById(Long id);

	PersonaEntity save(PersonaEntity persona);
	
	void deleteById(Long id);
	

}
