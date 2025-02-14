import React from "react";
import styles from "./CollectionItem.module.css";
import HorizontalLine from "../HorizontalLine/HorizontalLine";
import { useNavigate } from "react-router-dom";

const CollectionItem = ({ item, index }) => {
    const navigate = useNavigate();

    const formatDuration = (ms) => {
        const minutes = Math.floor(ms / 60000);
        const seconds = ((ms % 60000) / 1000).toFixed(0);
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    };

    const handleArtistClick = (e, artistId) => {
        e.stopPropagation();
        navigate(`/artist/${artistId}`);
    };

    const handleItemClick = () => {
        navigate(`/play?v=${item.id}`);
    };

    return (
        <>
            {index === 0 && <HorizontalLine />}
            <div className={styles.item} onClick={handleItemClick}>
                <div className={styles.number}>{index + 1}</div>
                <div className={styles.details}>
                    <p className={styles.name}>{item.name}</p>
                    <p className={styles.artist}>
                        {item.artists.map((artist, index) => (
                            <React.Fragment key={artist.id}>
                                {index > 0 && ", "}
                                <span
                                    className={styles.artistLink}
                                    onClick={(e) => handleArtistClick(e, artist.id)}
                                >
                                    {artist.name}
                                </span>
                            </React.Fragment>
                        ))}
                    </p>
                    <p className={styles.duration}>{formatDuration(item.duration_ms)}</p>
                </div>
            </div>
            <HorizontalLine />
        </>
    );
};

export default CollectionItem;