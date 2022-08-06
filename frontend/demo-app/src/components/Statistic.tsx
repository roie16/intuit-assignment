import React from "react";
import {H5, H6, Icon, Intent, Popover, PopoverInteractionKind, Position} from "@blueprintjs/core";
import {TransactionStatistics} from "../data/TransactionStatistics";


export const Statistic = ({
                              lat,
                              lng,
                              transactionStatistics
                          }: { lat: number, lng: number, transactionStatistics: TransactionStatistics }) => {
    return (
        <Popover
            interactionKind={PopoverInteractionKind.CLICK}
            popoverClassName="bp4-popover-content-sizing"
            position={Position.TOP}>
            <Icon icon={"map-marker"} size={50} intent={Intent.PRIMARY}/>
            <div>
                <H5>More Info on: {transactionStatistics.country}</H5>
                <H6>% {100 - transactionStatistics.passPercentage} blocked</H6>
                <H6>% {transactionStatistics.passPercentage} passed</H6>
                <H6>% {transactionStatistics.reviewPercentage} review</H6>
                <H6>{transactionStatistics.totalEventCount} events</H6>
            </div>
        </Popover>
    );

}