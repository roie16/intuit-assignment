import React, {useContext, useEffect, useState} from "react";
import {createRsocketClient} from "../reactor/RsocketClientCreator";
import {Transaction, TransactionWithTimeout} from "../data/Transaction";
import {ReactiveSocket} from "rsocket-types";
import {LiveMarker} from "./LiveMarker";
import GoogleMapReact from "google-map-react";
import {Switch} from "@blueprintjs/core";
import {DashboardContext} from "../context/DashboardContext";


export const LiveMarkers = () => {

    const [onlyHigh, setOnlyHigh] = useState(false);
    const [markers, setMarkers] = useState<TransactionWithTimeout[]>([])
    const dashboardContext = useContext(DashboardContext);

    let rsocket: ReactiveSocket<any, any> | null = null;


    useEffect(() => {
        getLiveSubscriptionFromServer().then(value => {
            rsocket = value
        });
        return () => {
            console.log("closing rsocket");
            rsocket?.close();
        }
    }, []);


    function renderElements() {
        let toPresent = markers;
        if (!dashboardContext.mint) {
            toPresent = toPresent.filter(value => !value.transaction.product.toLowerCase().includes("mint"))
        }
        if (!dashboardContext.quickBooks) {
            toPresent = toPresent.filter(value => !value.transaction.product.toLowerCase().includes("quickbooks"))
        }
        if (onlyHigh) {
            toPresent = toPresent.filter(value => value.transaction.severity.toLowerCase().includes("high"))
        }
        return toPresent
            .map((value, index) => <LiveMarker key={index} lat={value.transaction.latitude}
                                               lng={value.transaction.longitude}
                                               severity={value.transaction.severity}
                                               country={value.transaction.country}/>);
    }

    return (
        <>
            <Switch style={{
                position: "absolute",
                top: 20,
                left: 300,
                zIndex: "100"
            }} checked={onlyHigh} label="Show only high risk elements" onChange={() => setOnlyHigh(!onlyHigh)} large={true}/>
            <GoogleMapReact
                bootstrapURLKeys={{key: "AIzaSyBTr9_03VY09ZIRxtXOOataYzi98mgQcNE"}}
                defaultCenter={{lat: 10.99835602, lng: 77.01502627}}
                defaultZoom={1}>
                {renderElements()}
            </GoogleMapReact>
        </>
    );


    async function getLiveSubscriptionFromServer() {
        const rsocket = await createRsocketClient();
        rsocket.requestStream({metadata: String.fromCharCode('live'.length) + 'live'}).subscribe({
            onNext: (msg) => {
                const transaction: Transaction = JSON.parse(JSON.stringify(msg)).data;
                const TransactionWithTimeout: TransactionWithTimeout = {transaction: transaction, timeout: Date.now()}
                markers.push(TransactionWithTimeout)
                let newMarker = [...markers]
                newMarker = newMarker.filter(value => Date.now() - value.timeout < 30000) // remove after 30 seconds
                setMarkers(newMarker);
            },
            onComplete: () => {
                console.log(`requestStream completed`);
            },
            onError: (error) => {
                console.error(error);
            },
            onSubscribe: subscription => {
                subscription.request(100000000); // set it to some max value this is the subscription timeout
            }
        })
        return rsocket;
    }
}