import { getFocusStyle, getTheme, Icon, ITheme, List, mergeStyleSets, SearchBox, Spinner, SpinnerSize } from "@fluentui/react";
import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router";
import Route from "../models/Route";
import { ApiService } from "../services/ApiService";
import { RouteView } from "./RouteView";
import "./RouteListView.css";

type Props = {
    apiService: ApiService
}

const theme: ITheme = getTheme();
const { palette, semanticColors, fonts } = theme;

const classNames = mergeStyleSets({
    itemCell: [
      getFocusStyle(theme, { inset: -1 }),
      {
        minHeight: 54,
        padding: 10,
        boxSizing: 'border-box',
        borderBottom: `1px solid ${semanticColors.bodyDivider}`,
        display: 'flex',
        selectors: {
          '&:hover': { background: palette.neutralLight },
        },
      },
    ],
    itemImage: {
      flexShrink: 0,
    },
    itemContent: {
      marginLeft: 10,
      overflow: 'hidden',
      flexGrow: 1,
    },
    itemName: [
      fonts.xLarge,
      {
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
      },
    ],
    itemIndex: {
      fontSize: fonts.small.fontSize,
      color: palette.neutralTertiary,
      marginBottom: 10,
    },
    chevron: {
      alignSelf: 'center',
      marginLeft: 10,
      color: palette.neutralTertiary,
      fontSize: fonts.large.fontSize,
      flexShrink: 0,
    },
  });

export function RouteListView({apiService}: Props) {
    const params = useParams();
    const history = useHistory();
    const [routes, setRoutes] = useState<Route[]>([]);
    const [search, setSearch] = useState<string>("");
    const { id }: any = params;
    
    useEffect(() => {
        apiService
            .listRoutes()
            .then(routes => setRoutes(routes))
            // eslint-disable-next-line
    }, []);

    const onRenderCell = (route?: Route, index?: number): JSX.Element => {
        const isRouteView = route?.id === id;
        const toggleView = () => {
            if(isRouteView){
                history.push("/");
            } else {
                history.push(`/route/${route?.id}`);
            }
        };
        return (
            <div>
                {route && (
                    <>
                    <div className={classNames.itemCell} data-is-focusable={true} onClick={toggleView}>
                        <div className={classNames.itemContent}>
                            <div className={classNames.itemName}>{route.name}</div>
                            <div dangerouslySetInnerHTML={{__html: route.description}}/>
                        </div>
                        <Icon className={classNames.chevron} iconName="ChevronRight" />
                    </div>
                    {isRouteView && (
                        <RouteView id={route.id} apiService={apiService}/>
                    )}
                    </>
                )}
            </div>
        )
    }

    const filteredRoutes = (search === "") ? routes : routes.filter(t => t.name.toLowerCase().includes(search.toLowerCase()) || t.description.toLowerCase().includes(search.toLowerCase()))

    return (
        <div>
            <div className="searchBox">
                <div>
                    <SearchBox placeholder="Buscar rutas por Castilla y León" onChange={search => setSearch(search?.target.value || "")}/>
                </div>
            </div>
            <List items={filteredRoutes} onRenderCell={onRenderCell}/>
            {routes.length === 0 && <Spinner size={SpinnerSize.large} label="Cargando rutas por Castilla y León..."/>}
        </div>
    );
}