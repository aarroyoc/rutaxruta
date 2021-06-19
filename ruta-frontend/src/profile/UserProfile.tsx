import { DefaultButton, DetailsList, DetailsListLayoutMode, Selection, IColumn, Link, IObjectWithKey, SelectionMode } from "@fluentui/react";
import { useEffect, useMemo, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import TrackInfo from "../models/TrackInfo";
import User from "../models/User";
import { ApiService } from "../services/ApiService";
import "./UserProfile.css";

type Props = {
    apiService: ApiService,
    user: User | null,
}

function UserProfile({user, apiService}: Props) {
    const history = useHistory();
    const params = useParams();
    const { id }: any = params;
    const [tracks, setTracks] = useState<TrackInfo[]>([]);
    const [viewUser, setViewUser] = useState<User|null>(null);
    const [selectedTracks, setSelectedTracks] = useState<IObjectWithKey[]>();
    const selection = useMemo(
        () => new Selection({
            onSelectionChanged: () => {
                setSelectedTracks(selection.getSelection());
            },
            selectionMode: SelectionMode.multiple
        }),[]);

    useEffect(() => {
        apiService.getTrackInfoByUser(id).then(newTracks => setTracks(newTracks));
        apiService.getUser(id).then(newViewUser => setViewUser(newViewUser));
        // eslint-disable-next-line
    }, [id]);

    const columns = [
        {key: "name", name: "Nombre", fieldName: "name", minWidth: 100, maxWidth: 200, isResizable: true}
    ]

    const renderColumn = (item?: TrackInfo, index?: number, column?: IColumn) => {
        if(item !== undefined){
            const fieldContent = item[column?.fieldName as keyof TrackInfo] as string;
            switch(column?.key) {
                case "name":
                    return <Link onClick={() => history.push(`/track/${item.trackId}`)}>{item.name}</Link>;
                default:
                    return <span>{fieldContent}</span>;
            }
        }
        
    };

    const deleteTracks = () => {
        selectedTracks?.forEach((track: any) => {
            apiService.deleteTrack(track.trackId);
        })
    };

    const copyUrlToClipboard = () => {
        navigator.clipboard.writeText(window.location.href);
    }

    return (
        <div className="userProfile">
            <div className="userProfileBio">
                <img src={viewUser?.picture} alt="User avatar"/>
                <h2>{viewUser?.name}</h2>
                <DefaultButton text="Copiar enlace al perfil" onClick={() => copyUrlToClipboard()}/>
                {user?.id === viewUser?.id && (
                    <DefaultButton text="Borrar tracks seleccionados" onClick={() => deleteTracks()} disabled={selectedTracks?.length === 0}/>
                )}
            </div>
            <DetailsList
                items={tracks}
                columns={columns}
                selection={selection}
                onRenderItemColumn={renderColumn}
                layoutMode={DetailsListLayoutMode.justified}
                onItemInvoked={(item) => history.push(`/track/${item.trackId}`)}
            />
        </div>
    );
}

export default UserProfile;