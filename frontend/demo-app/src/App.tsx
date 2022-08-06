import React, {useState} from 'react';
import './App.css';
import {LiveMarkers} from "./components/LiveMarkers";
import {DashboardContext} from "./context/DashboardContext";
import {Statistics} from "./components/Statistics";
import {AppSideBar} from "./components/AppSideBar";

function App() {

    const [isRealTimeData, setIsRealTimeData] = useState(true);
    const [quickBooks, setQuickBooks] = useState(true);
    const [mint, setMint] = useState(true);


    return (
        <DashboardContext.Provider
            value={{quickBooks, setQuickBooks, mint, setMint, isRealTimeData, setIsRealTimeData}}>
            <div style={{height: '100vh', width: '100%', display: "flex"}}>
                <AppSideBar/>
                <>
                    {isRealTimeData ? <LiveMarkers/> : <Statistics/>}
                </>
            </div>
        </DashboardContext.Provider>
    );
}

export default App;
