import React from 'react';
import { Box, keyframes } from '@mui/material';

const float = keyframes`
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
`;

const rotate = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
`;

const pulse = keyframes`
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.7;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
`;

const AnimatedIcon = ({ children, animation = 'float', duration = '3s' }) => {
  const getAnimation = () => {
    switch (animation) {
      case 'float':
        return float;
      case 'rotate':
        return rotate;
      case 'pulse':
        return pulse;
      default:
        return float;
    }
  };

  return (
    <Box
      sx={{
        animation: `${getAnimation()} ${duration} ease-in-out infinite`,
        display: 'inline-block',
      }}
    >
      {children}
    </Box>
  );
};

export default AnimatedIcon;
