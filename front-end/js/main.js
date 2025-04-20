// API Configuration
const API_BASE_URL = '/api';
const ENDPOINTS = {
    posts: `${API_BASE_URL}/posts`,
    post: (id) => `${API_BASE_URL}/posts/${id}`,
};

// State Management
let currentPage = 1;
let isLoading = false;
let hasMorePosts = true;

// DOM Elements
const postsContainer = document.getElementById('postsContainer');
const loadingSpinner = document.getElementById('loadingSpinner');
const createPostModal = document.getElementById('createPostModal');
const createPostForm = document.getElementById('createPostForm');
const searchInput = document.getElementById('searchInput');
const searchButton = document.getElementById('searchButton');
const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
const navLinks = document.querySelector('.nav-links');

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    loadPosts();
    setupEventListeners();
    setupIntersectionObserver();
});

// Setup Event Listeners
function setupEventListeners() {
    // Mobile Menu Toggle
    mobileMenuBtn.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });

    // Search Functionality
    searchButton.addEventListener('click', handleSearch);
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleSearch();
    });

    // Create Post Form
    createPostForm.addEventListener('submit', handleCreatePost);

    // Close Modal
    document.querySelector('.close-modal').addEventListener('click', () => {
        createPostModal.style.display = 'none';
    });

    // Close Modal on Outside Click
    window.addEventListener('click', (e) => {
        if (e.target === createPostModal) {
            createPostModal.style.display = 'none';
        }
    });
}

// API Functions
async function fetchPosts(page = 1, searchQuery = '') {
    try {
        showLoading();
        const url = new URL(ENDPOINTS.posts);
        url.searchParams.append('page', page);
        if (searchQuery) url.searchParams.append('search', searchQuery);

        const response = await fetch(url);
        if (!response.ok) throw new Error('Failed to fetch posts');
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching posts:', error);
        showError('Failed to load posts. Please try again later.');
        return { posts: [], hasMore: false };
    } finally {
        hideLoading();
    }
}

async function createPost(postData) {
    try {
        const response = await fetch(ENDPOINTS.posts, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(postData),
        });

        if (!response.ok) throw new Error('Failed to create post');
        
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error creating post:', error);
        throw error;
    }
}

async function deletePost(postId) {
    try {
        const response = await fetch(ENDPOINTS.post(postId), {
            method: 'DELETE',
        });

        if (!response.ok) throw new Error('Failed to delete post');
        
        return true;
    } catch (error) {
        console.error('Error deleting post:', error);
        throw error;
    }
}

// UI Functions
function createPostElement(post) {
    const postElement = document.createElement('article');
    postElement.className = 'post-card fade-in';
    postElement.innerHTML = `
        <img src="${post.image || 'https://via.placeholder.com/300x200'}" alt="${post.title}" class="post-image">
        <div class="post-content">
            <h2 class="post-title">${post.title}</h2>
            <p class="post-excerpt">${post.excerpt || post.content.substring(0, 150)}...</p>
            <div class="post-actions">
                <button class="btn-primary" onclick="viewPost(${post.id})">Read More</button>
                <button class="btn-delete" onclick="deletePost(${post.id})">Delete</button>
            </div>
        </div>
    `;
    return postElement;
}

function showLoading() {
    isLoading = true;
    loadingSpinner.style.display = 'flex';
}

function hideLoading() {
    isLoading = false;
    loadingSpinner.style.display = 'none';
}

function showError(message) {
    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    postsContainer.appendChild(errorElement);
    setTimeout(() => errorElement.remove(), 5000);
}

// Event Handlers
async function handleCreatePost(e) {
    e.preventDefault();
    
    const title = document.getElementById('postTitle').value;
    const content = document.getElementById('postContent').value;

    try {
        await createPost({ title, content });
        createPostModal.style.display = 'none';
        createPostForm.reset();
        loadPosts(true); // Reload posts from the beginning
    } catch (error) {
        showError('Failed to create post. Please try again.');
    }
}

async function handleSearch() {
    const searchQuery = searchInput.value.trim();
    currentPage = 1;
    await loadPosts(true, searchQuery);
}

async function handleDeletePost(postId) {
    if (!confirm('Are you sure you want to delete this post?')) return;

    try {
        await deletePost(postId);
        loadPosts(true); // Reload posts from the beginning
    } catch (error) {
        showError('Failed to delete post. Please try again.');
    }
}

// Infinite Scroll
function setupIntersectionObserver() {
    const observer = new IntersectionObserver(
        (entries) => {
            if (entries[0].isIntersecting && !isLoading && hasMorePosts) {
                loadPosts();
            }
        },
        { threshold: 0.5 }
    );

    const sentinel = document.createElement('div');
    sentinel.className = 'scroll-sentinel';
    document.body.appendChild(sentinel);
    observer.observe(sentinel);
}

// Main Load Function
async function loadPosts(reset = false, searchQuery = '') {
    if (reset) {
        currentPage = 1;
        postsContainer.innerHTML = '';
        hasMorePosts = true;
    }

    if (isLoading || !hasMorePosts) return;

    const { posts, hasMore } = await fetchPosts(currentPage, searchQuery);
    
    posts.forEach(post => {
        const postElement = createPostElement(post);
        postsContainer.appendChild(postElement);
    });

    hasMorePosts = hasMore;
    currentPage++;
}

// Accessibility
function setupAccessibility() {
    // Add ARIA labels
    document.querySelectorAll('button').forEach(button => {
        if (!button.getAttribute('aria-label')) {
            button.setAttribute('aria-label', button.textContent.trim());
        }
    });

    // Add keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && createPostModal.style.display === 'block') {
            createPostModal.style.display = 'none';
        }
    });
}

// Initialize
setupAccessibility(); 