import PopularUserCard from "./PopularUserCard";
import SearchUser from "../SearchUser/SearchUser";
import { useNavigate } from "react-router-dom";

const popularUser = [
  {
    id: 1,
    userImage: "https://randomuser.me/api/portraits/men/83.jpg",
    username: "dev_john",
    description: "Follows you"
  },
  {
    id: 2,
    userImage: "https://randomuser.me/api/portraits/women/14.jpg",
    username: "design_karen",
    description: "Followed by Alex"
  },
  {
    id: 3,
    userImage: "https://randomuser.me/api/portraits/men/56.jpg",
    username: "mark_the_dev",
    description: "New on platform"
  },
  {
    id: 4,
    userImage: "https://randomuser.me/api/portraits/women/75.jpg",
    username: "ui.ux.rina",
    description: "Popular creator"
  },
  {
    id: 5,
    userImage: "https://randomuser.me/api/portraits/men/12.jpg",
    username: "tech.guru",
    description: "Suggested for you"
  },
  {
    id: 6,
    userImage: "https://randomuser.me/api/portraits/women/90.jpg",
    username: "code_with_amy",
    description: "Follows you"
  }
];

const HomeRight = () => {
  const navigate = useNavigate();

  const handleUserClick = (userId) => {
    navigate(`/profile/${userId}`);
  };

  return (
    <div className="pr-5">
      <SearchUser handleClick={handleUserClick} />
      <div className="card p-5">
        <div className="flex justify-between py-5 items-center">
          <p className="font-semibold opacity-70">Suggestions for you</p>
          <p className="text-xs font-semibold opacity-95">View All</p>
        </div>

        <div className="space-y-5">
          {popularUser.map((item) => (
            <PopularUserCard
              key={item.id}
              image={item.userImage}
              username={item.username}
              description={item.description}
              onClick={() => handleUserClick(item.id)}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default HomeRight;
