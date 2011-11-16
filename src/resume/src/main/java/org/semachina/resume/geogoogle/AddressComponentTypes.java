package org.semachina.resume.geogoogle;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sgopalan
 * Date: 2/12/11
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public enum AddressComponentTypes {
    //indicates a precise street address.
    StreetAddress("street_address"),

    //indicates a named route (such as "US 101").
    Route("route"),

    //indicates a major intersection, usually of two major roads.
    Intersection("intersection"),

    //indicates a political entity. Usually, this type indicates a polygon of some civil administration
    Political("political"),

    //indicates the national political entity, and is typically the highest order type returned by the Geocoder
    Country("country"),

    // indicates a first-order civil entity below the country level. Within the United States, these administrative
    // levels are states. Not all nations exhibit these administrative levels.
    AdministrativeAreaLevel1("administrative_area_level_1"),

    //indicates a second-order civil entity below the country level. Within the United States, these administrative
    // levels are counties. Not all nations exhibit these administrative levels.
    AdministrativeAreaLevel2( "administrative_area_level_2" ),

    //indicates a third-order civil entity below the country level. This type indicates a minor civil division.
    // Not all nations exhibit these administrative levels.
    AdministrativeAreaLevel3("administrative_area_level_3"),

    //indicates a commonly-used alternative name for the entity.
    ColloquialArea("colloquial_area"),

    //indicates an incorporated city or town political entity.
    Locality("locality"),

    Sublocality("sublocality"),

    //indicates a named neighborhood
    Neighborhood("neighborhood"),

    //indicates a named location, usually a building or collection of buildings with a common name
    Premise("premise"),

    //indicates a first-order entity below a named location, usually a singular building within a collection
    // of buildings with a common name
    Subpremise("subpremise"),

    //indicates a postal code as used to address postal mail within the country.
    PostalCode("postal_code"),

    //indicates a prominent natural feature.
    NaturalFeature("natural_feature"),

    //indicates an airport.
    Airport("airport"),

    //indicates a named park.
    Park("park"),

    //indicates a named point of interest. Typically, these "POI"s are prominent local entities that don't easily
    // fit in another category such as "Empire State Building" or "Statue of Liberty."
    PointOfInterest("point_of_interest");

    private static final Map<String, AddressComponentTypes> typesMap = new HashMap<String, AddressComponentTypes>();

    static {
        for(AddressComponentTypes s : EnumSet.allOf(AddressComponentTypes.class))
        typesMap.put(s.getCode(), s);
    }

    public static AddressComponentTypes get(String code) {
        return typesMap.get( code );
    }

    private String code;

    private AddressComponentTypes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
