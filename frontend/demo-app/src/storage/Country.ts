import {TransactionStatistics} from "../data/TransactionStatistics";
import Geocode from "react-geocode";

export interface Country {
    lat: number;
    lng: number
}

export async function getCountry(statistic: TransactionStatistics) {
    const countryString = localStorage.getItem(statistic.country)
    let country: Country;
    if (countryString == null) {
        Geocode.setApiKey("AIzaSyBTr9_03VY09ZIRxtXOOataYzi98mgQcNE");
        const geoData = await Geocode.fromAddress(statistic.country)
        country = {
            lat: geoData.results.at(0).geometry.location.lat,
            lng: geoData.results.at(0).geometry.location.lng
        };
        localStorage.setItem(statistic.country, JSON.stringify(country))
    } else {
        country = JSON.parse(countryString);
    }
    return country
}