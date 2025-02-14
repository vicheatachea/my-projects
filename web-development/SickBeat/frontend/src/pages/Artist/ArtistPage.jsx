import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useSpotifyGet } from '../../hooks/useSpotifyGet';
import CardList from '../../components/CardList/CardList';
import styles from './ArtistPage.module.css';
import HorizontalLine from "../../components/HorizontalLine/HorizontalLine.jsx";

const ArtistPage = () => {
    const { id } = useParams();
    const { data: artist, error } = useSpotifyGet(`/api/spotify/artist/${id}`);

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    if (error) {
        return <div>Error loading artist data</div>;
    }

    if (!artist) {
        return <div>Loading...</div>;
    }

    const formatNumber = (num) => {
        if (num == null) {
            return 'N/A';
        }
        if (num >= 1000000) {
            return (num / 1000000).toFixed(1) + 'M';
        } else if (num >= 1000) {
            return (num / 1000).toFixed(1) + 'K';
        }
        return num.toString();
    };

    const capitalizeFirstLetter = (string) => {
        return string.split(' ').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
    };

    return (
        <section>
            <div className={styles.artistContainer}>
                <div className={styles.artistInfo}>
                    <h1>{artist.name}</h1>
                    <img
                        className={styles.artistImage}
                        src={artist.image_url}
                        alt={artist.name}
                    />
                </div>
                <div className={styles.artistDetails}>
                    <p>Genres: {artist.genres?.map(capitalizeFirstLetter).join(', ') || 'N/A'}</p>
                    <p>Followers: {formatNumber(artist.followers)}</p>
                    <p>Popularity: {artist.popularity}</p>
                </div>
            </div>
            <HorizontalLine />
            <CardList title="Albums" items={artist.albums} />
            <HorizontalLine />
            <CardList title="Top Tracks" items={artist.topTracks} />
            <HorizontalLine />
            <CardList title="Related Artists" items={artist.relatedArtists} />
        </section>
    );
};

export default ArtistPage;