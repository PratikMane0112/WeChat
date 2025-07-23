import {
  Alert,
  Backdrop,
  Button,
  Card,
  CircularProgress,
  Grid,
  Snackbar,
  Box,
  Fade,
  Zoom,
  Slide,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import RegistrationForm from "./RegisterForm";
import { Navigate, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import { Login } from "@mui/icons-material";
import LoginForm from "./LoginForm";
import ResetPasswordRequest from "./ResetPasswordRequest";
import { useSelector } from "react-redux";
import ResetPassword from "./ResetPassword";
import "./Authentication.css";

const Authentication = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [openSnakbar, setOpenSnakbar] = useState(false);  const [animationKey, setAnimationKey] = useState(0);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [imagesLoaded, setImagesLoaded] = useState(false);
  const { auth } = useSelector((store) => store);

  // High-quality 4K images that grab attention
  const heroImages = [
    "https://media.istockphoto.com/id/2181905986/photo/cafe-social-media-icon-or-friends-with-phone-for-communication-text-post-or-online-dating.jpg?s=612x612&w=0&k=20&c=SMMBjfUA_uR4zjiZLgrUH1DM6OO-0fmNoePGUTA7tjs=",
    "https://media.istockphoto.com/id/851693374/photo/friends-surfing-the-net-at-a-cafe.jpg?s=612x612&w=0&k=20&c=CfgZTbdJ56KQ7fvdIJzeQzVfW2GPSU20xGTybRfCUdQ=",
    "https://media.istockphoto.com/id/1189729357/photo/asian-woman-in-a-cafe-drinking-coffee-portrait-of-asian-woman-smiling-in-coffee-shop-cafe.jpg?s=612x612&w=0&k=20&c=ykl3UO9uC_SYdyuCpSOiF0-AbK0Du8mXLKkhHhQJQQo=",
    "https://media.istockphoto.com/id/817460976/photo/playful-asian-female-friends-taking-a-selfie-with-four-cell-phones.jpg?s=612x612&w=0&k=20&c=Ns5obBu4_1ZICfpZfgNIcVI-RhuGZN0Wcl-0WRx3TlE="
  ];

  const handleClose = () => {
    setOpenSnakbar(false);
  };

  // Preload images for smooth transitions
  useEffect(() => {
    const preloadImages = async () => {
      const loadPromises = heroImages.map((src) => {
        return new Promise((resolve) => {
          const img = new Image();
          img.onload = resolve;
          img.onerror = resolve; // Continue even if an image fails to load
          img.src = src;
        });
      });
      
      await Promise.all(loadPromises);
      setImagesLoaded(true);
    };

    preloadImages();
  }, []);

  useEffect(() => {
    if (auth.resetPasswordLink) {
      setOpenSnakbar(true);
    }
  }, [auth.resetPasswordLink]);

  useEffect(() => {
    // Trigger animation when route changes
    setAnimationKey(prev => prev + 1);
  }, [location.pathname]);

  useEffect(() => {
    // Auto-rotate images every 5 seconds, only after images are loaded
    if (!imagesLoaded) return;
    
    const interval = setInterval(() => {
      setCurrentImageIndex((prev) => (prev + 1) % heroImages.length);
    }, 5000);

    return () => clearInterval(interval);
  }, [heroImages.length, imagesLoaded]);
  // Create floating particles
  const FloatingParticles = () => (
    <div className="floating-particles">
      {[...Array(50)].map((_, i) => (
        <div
          key={i}
          className="particle"
          style={{
            left: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 8}s`,
            animationDuration: `${8 + Math.random() * 15}s`,
            width: `${2 + Math.random() * 6}px`,
            height: `${2 + Math.random() * 6}px`,
          }}
        />
      ))}
    </div>
  );

  return (
    <div className="auth-container">
      <FloatingParticles />
      
      {/* Unified container with merged image and form */}
      <Box className="unified-auth-layout">
        {/* Background image section */}
        <Box className="background-image-section">
          {!imagesLoaded && (
            <Box className="image-loader">
              <CircularProgress size={60} sx={{ color: 'white' }} />
            </Box>
          )}
          
          {imagesLoaded && heroImages.map((image, index) => (
            <Fade 
              key={index}
              in={index === currentImageIndex} 
              timeout={1500}
              style={{
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                zIndex: index === currentImageIndex ? 2 : 1,
              }}
            >
              <img
                className="background-image"
                src={image}
                alt={`Social Network Visual ${index + 1}`}
              />
            </Fade>
          ))}
          
          {/* Overlay gradient */}
          <Box className="image-overlay" />
          
          {/* Dynamic text overlay */}
          {/* <Fade in={imagesLoaded} timeout={2000}>
            <Box className="hero-text-overlay">
              <Box className="hero-title logo-animated">WeChat</Box>
              <Box className="hero-subtitle">Share Your World</Box>
            </Box>
          </Fade> */}
          
          {/* Image indicators */}
          {imagesLoaded && (
            <Box className="image-indicators">
              {heroImages.map((_, index) => (
                <Box
                  key={index}
                  onClick={() => setCurrentImageIndex(index)}
                  className={`indicator ${index === currentImageIndex ? 'active' : ''}`}
                />
              ))}
            </Box>
          )}
        </Box>

        {/* Form section overlay */}
        <Box className="form-overlay-section">
          <Zoom in={true} timeout={800}>
            <Box className="auth-card-merged card p-8">
              <Box className="form-header">
                <h1 className="logo logo-animated text-center">WeChat</h1>
                <h4 className="tagline logo-animated text-center">
                  Messages That Matter. Moments That Last
                </h4>
              </Box>

              <Box key={animationKey} className="form-container">
                <Slide direction="up" in={true} timeout={600}>
                  <div>
                    <Routes>
                      <Route path="/" element={<RegistrationForm />}/>
                      <Route path="/reset-password-req" element={<ResetPasswordRequest />}/>
                      <Route path="/reset-password" element={<ResetPassword />}/>
                      <Route path="/register" element={<RegistrationForm />}/>
                      <Route path="/login" element={<LoginForm />}/>
                    </Routes>
                  </div>
                </Slide>
              </Box>
            </Box>
          </Zoom>
        </Box>
      </Box><Snackbar
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        open={openSnakbar}
        autoHideDuration={6000}
        onClose={handleClose}
      >
        <Fade in={openSnakbar}>
          <Alert 
            className="success-alert"
            severity="success" 
            sx={{ 
              width: "100%",
              background: "linear-gradient(135deg, #4caf50, #45a049)",
              color: "white",
              "& .MuiAlert-icon": {
                color: "white"
              }
            }}
          >
            {auth.resetPasswordLink}
          </Alert>
        </Fade>
      </Snackbar>

      <Backdrop
        sx={{ 
          color: "#fff", 
          zIndex: (theme) => theme.zIndex.drawer + 1,
          background: "rgba(0, 0, 0, 0.8)",
        }}
        open={auth.loading}
      >
        <Box display="flex" flexDirection="column" alignItems="center" className="loading-container">
          <CircularProgress 
            color="inherit" 
            size={60}
            thickness={4}
          />
          <Box mt={2} fontSize="1.2rem" fontWeight="500">
            Loading...
          </Box>
        </Box>
      </Backdrop>
    </div>
  );
};

export default Authentication;
