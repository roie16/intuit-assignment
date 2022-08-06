import { RSocketClient, JsonSerializer, IdentitySerializer } from 'rsocket-core';
import RSocketWebsocketClient from "rsocket-websocket-client";


export async function createRsocketClient() {
    const setup = {
        keepAlive: 100000000,
        lifetime: 10000000,
        dataMimeType: 'application/json',
        metadataMimeType: 'message/x.rsocket.routing.v0',
    };
    console.log("connecting with RSocket...");
    const transport = new RSocketWebsocketClient({url: 'ws://localhost:8081/rsocket'});
    const client = new RSocketClient({ setup, transport, serializers: {data: JsonSerializer, metadata: IdentitySerializer}});
    return client.connect();
}