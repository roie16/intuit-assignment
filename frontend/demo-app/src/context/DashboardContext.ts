import React from "react";

export interface DashboardContext {
    quickBooks: boolean;

    setQuickBooks(quickBooks: boolean): void;

    mint: boolean;

    setMint(quickBooks: boolean): void;


    isRealTimeData: boolean;

    setIsRealTimeData(quickBooks: boolean): void;
}

export const dashboardContextInitialState: DashboardContext = {
    quickBooks: true,
    setQuickBooks: () => {
    },
    mint: true,
    setMint: () => {
    },
    isRealTimeData: true,
    setIsRealTimeData: () => {
    }
};

export const DashboardContext = React.createContext(dashboardContextInitialState);
