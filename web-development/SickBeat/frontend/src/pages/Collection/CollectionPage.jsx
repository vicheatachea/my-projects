import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./CollectionPage.module.css";
import CollectionItem from "../../components/CollectionItem/CollectionItem";
import { useSpotifyGet } from "../../hooks/useSpotifyGet";
import { useBackend } from "../../hooks/useBackend";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
	faBookmark,
	faPlay,
	faRandom,
} from "@fortawesome/free-solid-svg-icons";
import { toast } from "react-toastify";

const CollectionPage = ({ isAuthenticated }) => {
	const { type, id } = useParams();
	const navigate = useNavigate();
	const { data: collection, error } = useSpotifyGet(
		`/api/spotify/collection/${type}/${id}`
	);
	const { sendRequest } = useBackend();
	const [isSaved, setIsSaved] = useState(false);

	useEffect(() => {
		window.scrollTo(0, 0);
	}, []);

	useEffect(() => {
		if (isAuthenticated) {
			const checkIfSaved = async () => {
				try {
					const response = await sendRequest(
						`/api/collections/${id}`,
						"GET"
					);
					if (response.data) {
						setIsSaved(true);
					}
				} catch (error) {
					setIsSaved(false);
				}
			};
			checkIfSaved();
		}
	}, [id, isAuthenticated]);

	if (error) {
		return <div>Error: {error}</div>;
	}

	if (!collection) {
		return <div>Loading...</div>;
	}

	const handlePlayClick = () => {
		if (collection.tracks?.length > 0) {
			navigate(`/play?v=${collection.tracks[0].id}`);
		}
	};

	const handleShufflePlayClick = () => {
		if (collection.tracks?.length > 0) {
			const randomIndex = Math.floor(
				Math.random() * collection.tracks.length
			);
			navigate(`/play?v=${collection.tracks[randomIndex].id}`);
		}
	};

	const handleSaveToLibraryClick = async () => {
		const data = {
			id: collection.id,
			name: collection.name,
			type: type,
			songAmount: collection.total_tracks,
			image: collection.image_url,
		};
		const response = await sendRequest("/api/collections", "POST", data);
		setIsSaved(true);

		if (response.status === 201) {
			if (type === "album") {
				toast.success("Album saved to library");
			} else if (type === "playlist") {
				toast.success("Playlist saved to library");
			}
		} else {
			toast.error("Failed to save to library");
		}
	};

	const handleRemoveFromLibraryClick = async () => {
		const response = await sendRequest(`/api/collections/${id}`, "DELETE");
		setIsSaved(false);

		if (response.status === 200) {
			if (type === "album") {
				toast.success("Album removed from library");
			} else if (type === "playlist") {
				toast.success("Playlist removed from library");
			}
		} else {
			toast.error("Failed to remove from library");
		}
	};

	return (
		<section className={styles.container}>
			<div className={styles.header}>
				<div className={styles.left}>
					<div className={styles.imageWrapper}>
						<img
							className={styles.playlistImage}
							src={collection.image_url}
							alt={collection.name}
						/>
					</div>
					<div className={styles.headerText}>
						<p className={styles.smallText}>{type.toUpperCase()}</p>
						<h1 className={styles.title}>{collection.name}</h1>
						<p className={styles.smallText}>
							Total Tracks: {collection.total_tracks}
						</p>
						{collection.release_date && (
							<p className={styles.smallText}>
								Release Date: {collection.release_date}
							</p>
						)}
					</div>
				</div>
				<div className={styles.right}>
					<button className={styles.button} onClick={handlePlayClick}>
						<FontAwesomeIcon
							icon={faPlay}
							className={styles.iconSpacing}
						/>{" "}
						Play
					</button>
					<button
						className={styles.button}
						onClick={handleShufflePlayClick}
					>
						<FontAwesomeIcon
							icon={faRandom}
							className={styles.iconSpacing}
						/>{" "}
						Shuffle Play
					</button>
					{isAuthenticated && (
						<button
							className={styles.button}
							onClick={
								isSaved
									? handleRemoveFromLibraryClick
									: handleSaveToLibraryClick
							}
						>
							{isSaved ? (
								<>
									<FontAwesomeIcon
										icon={faBookmark}
										className={styles.iconSpacing}
									/>{" "}
									Saved to Library
								</>
							) : (
								"Save to Library"
							)}
						</button>
					)}
				</div>
			</div>
			<div className={styles.items}>
				{collection.tracks?.length > 0 ? (
					collection.tracks.map((track, index) => (
						<CollectionItem
							key={track.id}
							item={track}
							index={index}
						/>
					))
				) : (
					<p>No tracks available</p>
				)}
			</div>
		</section>
	);
};

export default CollectionPage;
