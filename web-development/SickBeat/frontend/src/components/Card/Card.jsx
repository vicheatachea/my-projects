import React from "react";
import styles from "./Card.module.css";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

function Card({ item }) {
    const navigate = useNavigate();
    const validArtists = Array.isArray(item.artists)
        ? item.artists.filter(artist => artist && artist.name)
        : [];

    const handleArtistClick = (artistId) => {
        navigate(`/artist/${artistId}`);
    };

    const handleCardClick = () => {
        if (item.type === "track") {
            if (item.preview_url === "none") {
                toast.error("This track does not have a preview URL.");
                return;
            }
            navigate(`/play?v=${item.id}`);
        } else if (item.type === "album" || item.type === "playlist") {
            navigate(`/collection/${item.type}/${item.id}`);
        } else if (item.type === "artist") {
            navigate(`/artist/${item.id}`);
        }
    };

    return (
        <article className={styles.card} onClick={handleCardClick}>
            <img className={styles.image} src={item.image_url} alt={item.name} />
            <p className={styles.title}>{item.name}</p>
            {item.type === "track" || item.type === "album" ? (
                <p className={styles.content}>
                    {validArtists.map((artist, index) => (
                        <React.Fragment key={artist.id}>
                            <span
                                className={styles.artistLink}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleArtistClick(artist.id);
                                }}
                            >
                                {artist.name}
                            </span>
                            {index < validArtists.length - 1 ? ', ' : ''}
                        </React.Fragment>
                    ))}
                </p>
            ) : item.type === "playlist" ? (
                <p className={styles.content}>{item.owner?.name}</p>
            ) : item.type === "artist" ? (
                <p className={styles.content}>{formatNumber(item.followers)} followers</p>
            ) : null}
        </article>
    );
}

export default Card;
