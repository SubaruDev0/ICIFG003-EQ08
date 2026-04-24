package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PersonaEntity;
import com.example.demo.interfaces.IPersonaService;
import com.example.demo.repository.PersonaRepository;

@Service
public class PersonaService implements IPersonaService {

	
	@Autowired
	private PersonaRepository personaRepository;
	
	@Override
	public List<PersonaEntity> findAll() {
		// TODO Auto-generated method stub
		return (List<PersonaEntity>) personaRepository.findAll();
	}

	@Override
	public PersonaEntity findById(Long id){
		return personaRepository.findById(id).orElse(null);

	}

	@Override
	public PersonaEntity save(PersonaEntity persona) {
		
		return personaRepository.save(persona);

	}

	@Override
	public void deleteById(Long id) {
		personaRepository.deleteById(id);
	}

	

}
