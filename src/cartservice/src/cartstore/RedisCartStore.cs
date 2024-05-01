using System;
using System.Linq;
using System.Threading.Tasks;
using Grpc.Core;
using Microsoft.Extensions.Caching.Distributed;
using Google.Protobuf;
using Hipstershop;

namespace cartservice.cartstore
{
    public class RedisCartStore : ICartStore
    {
        private readonly IDistributedCache _cache;
        private const double DiscountThreshold = 100.0;
        private const double DiscountRate = 0.10; // 10% discount

        public RedisCartStore(IDistributedCache cache)
        {
            _cache = cache;
        }

        public async Task AddItemAsync(string userId, string productId, int quantity)
        {
            Console.WriteLine($"AddItemAsync called with userId={userId}, productId={productId}, quantity={quantity}");
            try
            {
                Cart cart;
                var value = await _cache.GetAsync(userId);
                if (value == null)
                {
                    cart = new Cart();
                    cart.UserId = userId;
                    cart.Items.Add(new CartItem { ProductId = productId, Quantity = quantity });
                }
                else
                {
                    cart = Cart.Parser.ParseFrom(value);
                    var existingItem = cart.Items.SingleOrDefault(i => i.ProductId == productId);
                    if (existingItem == null)
                    {
                        cart.Items.Add(new CartItem { ProductId = productId, Quantity = quantity });
                    }
                    else
                    {
                        existingItem.Quantity += quantity;
                    }
                }
                await _cache.SetAsync(userId, cart.ToByteArray());
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Can't access cart storage. {ex.Message}"));
            }
        }

        public async Task EmptyCartAsync(string userId)
        {
            Console.WriteLine($"EmptyCartAsync called with userId={userId}");

            try
            {
                var cart = new Cart();
                await _cache.SetAsync(userId, cart.ToByteArray());
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Can't access cart storage. {ex.Message}"));
            }
        }

        public async Task<Cart> GetCartAsync(string userId)
        {
            Console.WriteLine($"GetCartAsync called with userId={userId}");

            try
            {
                var value = await _cache.GetAsync(userId);
                if (value != null)
                {
                    return Cart.Parser.ParseFrom(value);
                }
                // We decided to return an empty cart in cases when user wasn't in the cache before
                return new Cart();
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Can't access cart storage. {ex.Message}"));
            }
        }

        public async Task<double> GetCartTotalWithDiscountAsync(string userId)
        {
            try
            {
                var cart = await GetCartAsync(userId);
                double total = cart.Items.Sum(item => item.Price * item.Quantity);

                if (total > DiscountThreshold)
                {
                    double discount = total * DiscountRate;
                    total -= discount;
                    Console.WriteLine($"Discount of {discount:C} applied. New total: {total:C}");
                }

                return total;
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Error calculating cart total with discount: {ex.Message}"));
            }
        }

        public bool Ping()
        {
            try
            {
                // Simply checks if the cache is accessible
                return _cache != null;
            }
            catch (Exception)
            {
                return false;
            }
        }
    }
}
