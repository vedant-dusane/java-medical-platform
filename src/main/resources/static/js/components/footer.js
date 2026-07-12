/**
 * footer.js
 * Renders a static, reusable footer into the #footer div on every page.
 * The footer contains branding and three link columns: Company, Support, Legals.
 */

function renderFooter() {
  const footer = document.getElementById("footer");
  if (!footer) return;

  footer.innerHTML = `
    <footer class="footer">
      <div class="footer-brand">
        <h4>Smart Clinic</h4>
        <p>© ${new Date().getFullYear()} Smart Clinic. All rights reserved.</p>
      </div>

      <div class="footer-links">
        <div class="footer-column">
          <h5>Company</h5>
          <a href="#">About</a>
          <a href="#">Careers</a>
          <a href="#">Press</a>
        </div>

        <div class="footer-column">
          <h5>Support</h5>
          <a href="#">Account</a>
          <a href="#">Help Center</a>
          <a href="#">Contact</a>
        </div>

        <div class="footer-column">
          <h5>Legals</h5>
          <a href="#">Terms</a>
          <a href="#">Privacy Policy</a>
          <a href="#">Licensing</a>
        </div>
      </div>
    </footer>
  `;
}

// Auto-render when script loads
renderFooter();
