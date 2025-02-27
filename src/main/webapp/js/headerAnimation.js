const particlesContainer = document.getElementById('particles');
const particleCount = 20;

for (let i = 0; i < particleCount; i++) {
    createParticle();
}

function createParticle() {
    const particle = document.createElement('div');
    particle.classList.add('particle');
    
    // Random size between 3px and 8px
    const size = Math.random() * 5 + 3;
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    
    // Random position
    const posX = Math.random() * 100;
    const posY = Math.random() * 100;
    particle.style.left = `${posX}%`;
    particle.style.top = `${posY}%`;
    
    // Random animation duration between
    const duration = Math.random() * 10 + 15;
    particle.style.animation = `float ${duration}s linear infinite`;
    
    // Random delay
    const delay = Math.random() * 10;
    particle.style.animationDelay = `${delay}s`;
    
    particlesContainer.appendChild(particle);
}