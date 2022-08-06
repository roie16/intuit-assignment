import React, {useContext, useEffect, useState} from "react";
import GoogleMapReact from "google-map-react";
import Geocode from "react-geocode";
import {TransactionStatistics} from "../data/TransactionStatistics";
import {DashboardContext} from "../context/DashboardContext";
import {Statistic} from "./Statistic";


export const Statistics = () => {

    const dashboardContext = useContext(DashboardContext);
    const [stats, setStats] = useState<TransactionStatistics[]>([])

    async function setCountryGeoLocationOnStat(statistic: TransactionStatistics) {
        const withCoordinates = {...statistic};
        const  geoData = await Geocode.fromAddress(statistic.country)
        withCoordinates.lat = geoData.results.at(0).geometry.location.lat
        withCoordinates.lng = geoData.results.at(0).geometry.location.lng
        return withCoordinates;
    }

    const getStatisticsFromServer = async () => {
        const response = await fetch('http://localhost:8080/v1/stats')
        const data = await response.json()
        const statistics: TransactionStatistics[] = data.stats;
        Geocode.setApiKey("AIzaSyBTr9_03VY09ZIRxtXOOataYzi98mgQcNE");
        const withCoordinates =  await Promise.all(statistics.map(statistic => setCountryGeoLocationOnStat(statistic)));
        setStats(withCoordinates);
    }

    useEffect(() => {
            console.log("reloading stats");
            getStatisticsFromServer();
        }
        , [dashboardContext.isRealTimeData]);

    function renderElements() {
        return stats.map((stat, index) => {
            return <Statistic key={index} lat={stat.lat} lng={stat.lng} transactionStatistics={stat}/>;
        })
    }

    return (
        <>
            <GoogleMapReact
                bootstrapURLKeys={{key: "AIzaSyBTr9_03VY09ZIRxtXOOataYzi98mgQcNE"}}
                defaultCenter={{lat: 10.99835602, lng: 77.01502627}}
                defaultZoom={1}>
                {renderElements()}
            </GoogleMapReact>
        </>
    );


}