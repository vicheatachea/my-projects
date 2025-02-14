import React from 'react';
import CardList from "../../components/CardList/CardList.jsx";
import {useSearchParams} from "react-router-dom";
import HorizontalLine from "../../components/HorizontalLine/HorizontalLine.jsx";
import {useSpotifyGet} from "../../hooks/useSpotifyGet.jsx";

function SearchResultsPage() {
    const [searchParams] = useSearchParams();
    const query = searchParams.get("q");

    const {
        data: searchTrack,
        error: searchTrackError
    } = useSpotifyGet(`/api/spotify/search?q=${query}&type=track`);
    const {
        data: searchArtist,
        error: searchArtistError
    } = useSpotifyGet(`/api/spotify/search?q=${query}&type=artist`);
    const {
        data: searchAlbum,
        error: searchAlbumError
    } = useSpotifyGet(`/api/spotify/search?q=${query}&type=album`);
    const {
        data: searchPlaylist,
        error: searchPlaylistError
    } = useSpotifyGet(`/api/spotify/search?q=${query}&type=playlist`);

    return (
        <>
            <CardList title="Tracks" items={searchTrack} error={searchTrackError}/>
            <HorizontalLine/>
            <CardList title="Artists" items={searchArtist} error={searchArtistError}/>
            <HorizontalLine/>
            <CardList title="Albums" items={searchAlbum} error={searchAlbumError}/>
            <HorizontalLine/>
            <CardList title="Playlists" items={searchPlaylist} error={searchPlaylistError}/>
        </>
    );
}

export default SearchResultsPage;
