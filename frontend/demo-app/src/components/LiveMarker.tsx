import React from "react";

import {Icon, Intent} from "@blueprintjs/core";


export const LiveMarker = ({lat, lng, severity, country}: { lat: number, lng: number, severity: string , country: string}) => {
    const HIGH = "high";
    return (
        <>
            <p>{country}</p>
            <Icon icon={"full-circle"} size={10}
                  intent={HIGH.toLowerCase().includes(severity?.toLowerCase()) ? Intent.DANGER : Intent.SUCCESS}/>
        </>
    );

}