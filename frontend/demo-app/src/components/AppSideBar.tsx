import {Checkbox} from "@blueprintjs/core";
import React, {useContext} from "react";
import {Menu, MenuItem, ProSidebar, SidebarContent} from "react-pro-sidebar";
import {DashboardContext} from "../context/DashboardContext";

export const AppSideBar = () => {
    const dashboardContext = useContext(DashboardContext);

    return (
        <ProSidebar>
            <SidebarContent>
                <Menu iconShape="circle">
                    <MenuItem style={{alignSelf: "center"}}>Products</MenuItem>
                    <Checkbox checked={dashboardContext.quickBooks} label="Quickbooks"
                              onChange={() => dashboardContext.setQuickBooks(!dashboardContext.quickBooks)}
                              style={{left: 10}}/>
                    <Checkbox checked={dashboardContext.mint} label="Mint" onChange={() => {
                        dashboardContext.setMint(!dashboardContext.mint)
                    }} style={{left: 10}}/>
                </Menu>
                <Menu iconShape="circle">
                    <Checkbox checked={dashboardContext.isRealTimeData} label="Real Time Data" onChange={() => {
                        dashboardContext.setIsRealTimeData(!dashboardContext.isRealTimeData)
                    }} style={{top: 100, left: 10}}/>
                </Menu>
            </SidebarContent>
        </ProSidebar>
    );
}