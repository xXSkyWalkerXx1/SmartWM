package de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteElementRepository extends JpaRepository<WebsiteElement, Integer> {
}
