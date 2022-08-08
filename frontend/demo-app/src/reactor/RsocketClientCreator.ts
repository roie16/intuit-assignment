import {WellKnownMimeType} from "rsocket-composite-metadata";
import {RSocketConnector} from 'rsocket-core';
import {WebsocketClientTransport} from 'rsocket-websocket-client';


export const createRsocketClient = async () => {
    const connector = new RSocketConnector({
        transport: new WebsocketClientTransport({
            url: "ws://localhost:8080/rsocket",
        }),
        setup: {
            dataMimeType: WellKnownMimeType.APPLICATION_JSON.toString(),
            metadataMimeType: WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.toString()
        }
    });
    return connector.connect();
}
