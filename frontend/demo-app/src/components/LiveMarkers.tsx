import React, {useContext, useEffect, useState} from "react";
import {createRsocketClient} from "../reactor/RsocketClientCreator";
import {Transaction, TransactionWithTimeout} from "../data/Transaction";
import {LiveMarker} from "./LiveMarker";
import {MAX_REQUEST_N} from 'rsocket-core';
import GoogleMapReact from "google-map-react";
import {Switch} from "@blueprintjs/core";
import {DashboardContext} from "../context/DashboardContext";
import {useEffectOnce} from "../hooks/useEffectOnce";
import {RSocket} from "rsocket-core/dist/RSocket";
import {encodeCompositeMetadata, encodeRoute, WellKnownMimeType} from "rsocket-composite-metadata";
import {Buffer} from 'buffer';

window.Buffer = Buffer;

export const LiveMarkers = () => {

    const [onlyHigh, setOnlyHigh] = useState(false);
    const [markers, setMarkers] = useState<TransactionWithTimeout[]>([])
    const dashboardContext = useContext(DashboardContext);
    const [rsocket, setRSocket] = useState<RSocket | null>(null);

    useEffectOnce(() => {
        createRsocketClient().then((_rsocket) => {
            setRSocket(_rsocket);
        });
        return () => {
            rsocket?.close();
        };
        // @ts-ignore
    }, [createRsocketClient]);


    useEffect(() => {
        if (!rsocket) {
            return;
        }
        const map = new Map();
        map.set(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, encodeRoute("live"));
        const compositeMetaData = encodeCompositeMetadata(map);
        const stream = rsocket.requestStream({metadata: compositeMetaData, data: null},
            MAX_REQUEST_N,
            {
                onError(error) {
                    console.error(error);
                },
                onComplete() {
                    console.error('peer stream complete');
                },
                onNext(payload) {
                    // @ts-ignore payload.data is not recognized as buffer
                    const string = Buffer.from(payload.data).toString('utf8');
                    const transaction: Transaction = JSON.parse(string);
                    const TransactionWithTimeout: TransactionWithTimeout = {
                        transaction: transaction,
                        timeout: Date.now()
                    }
                    markers.push(TransactionWithTimeout)
                    let newMarker = [...markers]
                    newMarker = newMarker.filter(value => Date.now() - value.timeout < 30000) // remove after 30 seconds
                    setMarkers(newMarker);
                },
                onExtension(extendedType, content, canBeIgnored) {
                }
            });
        return () => {
            stream?.cancel();
        };
    }, [rsocket]);


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
            }} checked={onlyHigh} label="Show only high risk elements" onChange={() => setOnlyHigh(!onlyHigh)}
                    large={true}/>
            <GoogleMapReact
                bootstrapURLKeys={{key: "AIzaSyBTr9_03VY09ZIRxtXOOataYzi98mgQcNE"}}
                defaultCenter={{lat: 10.99835602, lng: 77.01502627}}
                defaultZoom={1}>
                {renderElements()}
            </GoogleMapReact>
        </>
    );

}