@SuppressWarnings("Java9RedundantRequiresStatement")
module de.tud.inf.mmt.wmscrape {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires spring.boot.autoconfigure;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.boot;
    requires spring.aop;
    requires spring.tx;
    requires java.sql;
    requires java.persistence;
    requires java.management;
    requires java.annotation;
    requires org.hibernate.orm.core;
    requires commons.dbcp2;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.compress;

    requires org.seleniumhq.selenium.firefox_driver;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.remote_driver;

    requires mysql.connector.java;
    // #######################

    exports de.tud.inf.mmt.wmscrape;
    exports de.tud.inf.mmt.wmscrape.springdata;

    exports de.tud.inf.mmt.wmscrape.gui.login.controller;

    exports de.tud.inf.mmt.wmscrape.gui;
    exports de.tud.inf.mmt.wmscrape.gui.tabs;

    // import
    exports de.tud.inf.mmt.wmscrape.gui.tabs.imports.controller to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.imports.management to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.imports.data to spring.beans;

    // scraping
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.element to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.website to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.selection to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.description to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.extraction to spring.beans;

    // historic
    exports de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.website to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.element to spring.beans;

    // data
    exports de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.controller to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.management to spring.beans;

    exports de.tud.inf.mmt.wmscrape.gui.tabs.depots.data to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.accounts.data to spring.beans;

    // visualization
    exports de.tud.inf.mmt.wmscrape.gui.tabs.visualization.controller to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.visualization.management to spring.beans;
    exports de.tud.inf.mmt.wmscrape.gui.tabs.visualization.data to spring.beans;

    // dynamic db

    exports de.tud.inf.mmt.wmscrape.dynamicdb;
    exports de.tud.inf.mmt.wmscrape.dynamicdb.course;
    exports de.tud.inf.mmt.wmscrape.dynamicdb.exchange;
    exports de.tud.inf.mmt.wmscrape.dynamicdb.stock;
    exports de.tud.inf.mmt.wmscrape.dynamicdb.transaction to spring.beans;
    exports de.tud.inf.mmt.wmscrape.dynamicdb.watchlist;

    // #######################

    opens de.tud.inf.mmt.wmscrape;
    opens de.tud.inf.mmt.wmscrape.springdata;

    opens de.tud.inf.mmt.wmscrape.gui.login.controller;

    opens de.tud.inf.mmt.wmscrape.gui;
    opens de.tud.inf.mmt.wmscrape.gui.tabs;

    // import
    opens de.tud.inf.mmt.wmscrape.gui.tabs.imports.data to org.hibernate.orm.core, spring.core, javafx.base;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.imports.controller to javafx.fxml, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.imports.management to spring.core;

    // scraping
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller to spring.core, javafx.fxml;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data to javafx.base, javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.element to javafx.fxml, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.controller.website to javafx.fxml, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.enums to javafx.base, javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.selection to javafx.base, javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.element to javafx.base, javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.description to javafx.base, javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.data.correlation.identification to javafx.base, javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.gui to javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.website to javafx.fxml, org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.scraping.management.extraction to javafx.fxml, org.hibernate.orm.core, spring.core;

    // historic
    opens de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller to spring.core, javafx.fxml;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.website to spring.core, javafx.fxml;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.historic.controller.element to spring.core, javafx.base, javafx.fxml;

    // data
    opens de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.data;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.controller to spring.core, javafx.fxml;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.dbdata.management to spring.core;

    opens de.tud.inf.mmt.wmscrape.gui.tabs.depots.data to org.hibernate.orm.core, spring.core;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.accounts.data;

    // visualization
    opens de.tud.inf.mmt.wmscrape.gui.tabs.visualization.controller to spring.core, javafx.fxml;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.visualization.management to spring.core, javafx.fxml;
    opens de.tud.inf.mmt.wmscrape.gui.tabs.visualization.data to spring.core, javafx.fxml;

    // dynamic db
    opens de.tud.inf.mmt.wmscrape.dynamicdb;
    opens de.tud.inf.mmt.wmscrape.dynamicdb.course;
    opens de.tud.inf.mmt.wmscrape.dynamicdb.exchange;
    opens de.tud.inf.mmt.wmscrape.dynamicdb.stock;
    opens de.tud.inf.mmt.wmscrape.dynamicdb.transaction to spring.core, org.hibernate.orm.core;
    opens de.tud.inf.mmt.wmscrape.dynamicdb.watchlist;
}
