import React, { useState } from 'react';
import { Button, Box } from '@mui/material';
import { styled, keyframes } from '@mui/material/styles';

const shimmer = keyframes`
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
`;

const ripple = keyframes`
  to {
    transform: scale(4);
    opacity: 0;
  }
`;

const StyledButton = styled(Button)(({ theme }) => ({
  position: 'relative',
  overflow: 'hidden',
  background: 'linear-gradient(45deg, #58c7fa, #667eea)',
  borderRadius: '25px',
  padding: '12px 30px',
  color: 'white',
  border: 'none',
  fontSize: '16px',
  fontWeight: '600',
  textTransform: 'uppercase',
  letterSpacing: '1px',
  transition: 'all 0.3s ease',
  boxShadow: '0 4px 15px rgba(88, 199, 250, 0.4)',
  
  '&:hover': {
    transform: 'translateY(-2px)',
    boxShadow: '0 6px 20px rgba(88, 199, 250, 0.6)',
    background: 'linear-gradient(45deg, #667eea, #58c7fa)',
  },
  
  '&:active': {
    transform: 'translateY(0)',
  },
  
  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    background: 'linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%)',
    transform: 'translateX(-100%)',
    transition: 'transform 0.6s',
  },
  
  '&:hover::before': {
    transform: 'translateX(100%)',
  },
}));

const RippleEffect = styled('span')({
  position: 'absolute',
  borderRadius: '50%',
  background: 'rgba(255, 255, 255, 0.6)',
  transform: 'scale(0)',
  animation: `${ripple} 0.6s linear`,
  pointerEvents: 'none',
});

const AnimatedButton = ({ children, onClick, loading, ...props }) => {
  const [ripples, setRipples] = useState([]);

  const handleClick = (event) => {
    const rect = event.currentTarget.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;
    
    const newRipple = {
      x,
      y,
      size,
      id: Date.now(),
    };
    
    setRipples(prev => [...prev, newRipple]);
    
    setTimeout(() => {
      setRipples(prev => prev.filter(ripple => ripple.id !== newRipple.id));
    }, 600);
    
    if (onClick) {
      onClick(event);
    }
  };

  return (
    <StyledButton
      onClick={handleClick}
      disabled={loading}
      {...props}
    >
      {ripples.map(ripple => (
        <RippleEffect
          key={ripple.id}
          style={{
            left: ripple.x,
            top: ripple.y,
            width: ripple.size,
            height: ripple.size,
          }}
        />
      ))}
      {loading ? (
        <Box display="flex" alignItems="center" gap={1}>
          <Box
            sx={{
              width: 16,
              height: 16,
              border: '2px solid transparent',
              borderTop: '2px solid white',
              borderRadius: '50%',
              animation: 'spin 1s linear infinite',
              '@keyframes spin': {
                '0%': { transform: 'rotate(0deg)' },
                '100%': { transform: 'rotate(360deg)' },
              },
            }}
          />
          Loading...
        </Box>
      ) : (
        children
      )}
    </StyledButton>
  );
};

export default AnimatedButton;
