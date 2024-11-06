package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.owners;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Owner;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    public List<Owner> getAllOwners(){
        return ownerRepository.findAll();
    }

    public Owner findBy(int id) { return ownerRepository.getById(id); }

    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);
    }
}
